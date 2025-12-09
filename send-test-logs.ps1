$client = New-Object System.Net.Sockets.TcpClient('localhost', 9999)
$stream = $client.GetStream()
$encoding = [System.Text.Encoding]::UTF8

Write-Host "Sending log messages to TCP server on port 9999..."

# Message 1
$message1 = "2025-11-09 23:10:00 INFO User login successful - user: Jairo Rodriguez`r`n"
$bytes1 = $encoding.GetBytes($message1)
$stream.Write($bytes1, 0, $bytes1.Length)
$stream.Flush()
Write-Host "Sent: INFO message"
Start-Sleep -Seconds 1

# Message 2
$message2 = "2025-11-09 23:10:05 ERROR Database connection failed - timeout exceeded`r`n"
$bytes2 = $encoding.GetBytes($message2)
$stream.Write($bytes2, 0, $bytes2.Length)
$stream.Flush()
Write-Host "Sent: ERROR message"
Start-Sleep -Seconds 1

# Message 3
$message3 = "2025-11-09 23:10:10 WARN Cache miss for key: session_103117210`r`n"
$bytes3 = $encoding.GetBytes($message3)
$stream.Write($bytes3, 0, $bytes3.Length)
$stream.Flush()
Write-Host "Sent: WARN message"
Start-Sleep -Seconds 1

# Message 4
$message4 = "2025-11-09 23:10:15 DEBUG Processing request ID: rodrijai97`r`n"
$bytes4 = $encoding.GetBytes($message4)
$stream.Write($bytes4, 0, $bytes4.Length)
$stream.Flush()
Write-Host "Sent: DEBUG message"
Start-Sleep -Seconds 1

# Message 5 - JSON format
$message5 = '{"timestamp":"2025-11-09T23:10:20","level":"INFO","service":"payment-api","message":"Payment processed successfully","amount":150.50,"currency":"USD"}' + "`r`n"
$bytes5 = $encoding.GetBytes($message5)
$stream.Write($bytes5, 0, $bytes5.Length)
$stream.Flush()
Write-Host "Sent: JSON format message"
Start-Sleep -Seconds 1

# Message 6 - Apache/Nginx log format
$message6 = '192.168.1.100 - - [09/Nov/2025:23:10:25 +0000] "GET /api/users HTTP/1.1" 200 1234 "https://example.com" "Mozilla/5.0"' + "`r`n"
$bytes6 = $encoding.GetBytes($message6)
$stream.Write($bytes6, 0, $bytes6.Length)
$stream.Flush()
Write-Host "Sent: Apache/Nginx format message"
Start-Sleep -Seconds 1

# Message 7 - Simple plain text
$message7 = "Application started successfully on port 8080`r`n"
$bytes7 = $encoding.GetBytes($message7)
$stream.Write($bytes7, 0, $bytes7.Length)
$stream.Flush()
Write-Host "Sent: Simple plain text message"
Start-Sleep -Seconds 1

# Message 8 - Stack trace format (as single line message with \n inside)
$message8 = "2025-11-09 23:10:35 ERROR Exception occurred in transaction processing`njava.lang.NullPointerException: Cannot invoke method on null object`n    at com.example.service.PaymentService.process(PaymentService.java:45)`n    at com.example.controller.PaymentController.handlePayment(PaymentController.java:23)`r`n"
$bytes8 = $encoding.GetBytes($message8)
$stream.Write($bytes8, 0, $bytes8.Length)
$stream.Flush()
Write-Host "Sent: Stack trace format message"
Start-Sleep -Seconds 1

# Message 9 - CSV format
$message9 = "2025-11-09 23:10:40,INFO,auth-service,user_login,success,user_id=12345,ip=192.168.1.50`r`n"
$bytes9 = $encoding.GetBytes($message9)
$stream.Write($bytes9, 0, $bytes9.Length)
$stream.Flush()
Write-Host "Sent: CSV format message"
Start-Sleep -Seconds 1

# Message 10 - Syslog format
$message10 = "<134>Nov 09 23:10:45 webserver01 nginx[1234]: Connection closed by client 10.0.0.5`r`n"
$bytes10 = $encoding.GetBytes($message10)
$stream.Write($bytes10, 0, $bytes10.Length)
$stream.Flush()
Write-Host "Sent: Syslog format message"

# Wait for server to process all messages before closing
Start-Sleep -Seconds 2

$stream.Close()
$client.Close()

Write-Host "`nAll logs sent successfully! (10 messages total)"
