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

$stream.Close()
$client.Close()

Write-Host "`nAll logs sent successfully!"
