$ErrorActionPreference = 'Stop'
$client = Invoke-RestMethod -Method Post -Uri 'http://localhost:8081/api/client' -ContentType 'application/json' -Body '{"nom":"Soufiane","age":22}'
$body = @{marque='Renault';modele='Clio';matricule='12345-A-6';clientId=$client.id} | ConvertTo-Json
$car = Invoke-RestMethod -Method Post -Uri 'http://localhost:8082/api/car' -ContentType 'application/json' -Body $body
$direct = Invoke-RestMethod "http://localhost:8082/api/car/$($car.id)"
if ($direct.client.id -ne $client.id) { throw 'Client associé incorrect' }
$viaGateway = Invoke-RestMethod "http://localhost:8888/SERVICE-CAR/api/car/$($car.id)"
if ($viaGateway.client.nom -ne 'Soufiane') { throw 'Réponse Gateway incorrecte' }
Invoke-RestMethod 'http://localhost:8888/SERVICE-CLIENT/api/client' | ConvertTo-Json
Invoke-RestMethod 'http://localhost:8888/SERVICE-CAR/api/car' | ConvertTo-Json -Depth 5
Write-Host 'SUCCÈS : création, lecture, communication RestTemplate et routage Gateway.'
