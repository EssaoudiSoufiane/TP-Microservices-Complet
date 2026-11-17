$ErrorActionPreference = 'Stop'
$project = $PSScriptRoot
$processes = @()
try {
 foreach ($module in @('eureka-server','client-service','car-service','gateway')) {
  $jar = Join-Path $project "$module/target/$module-0.0.1-SNAPSHOT.jar"
  if (-not (Test-Path $jar)) { throw 'Compiler avec .\mvnw.cmd clean verify avant ce script.' }
  $runArgs = @('-jar', ('"' + $jar + '"'))
  if ($module -in @('client-service','car-service')) { $runArgs += '--spring.profiles.active=demo' }
  $processes += Start-Process java -ArgumentList $runArgs -WorkingDirectory $project -WindowStyle Hidden -PassThru -RedirectStandardOutput (Join-Path $project "$module.log") -RedirectStandardError (Join-Path $project "$module-error.log")
 }
 $deadline = (Get-Date).AddMinutes(3)
 do {
  Start-Sleep -Seconds 5
  $ready = $true
  foreach ($port in @(8761,8081,8082,8888)) {
   try { $null = Invoke-RestMethod "http://localhost:$port/actuator/health" -TimeoutSec 2 } catch { $ready = $false }
  }
  if ((Get-Date) -gt $deadline) { throw 'Delai de demarrage depasse : consulter les fichiers .log.' }
 } until ($ready)
 Write-Output 'Les quatre applications sont demarrees.'
 $success = $false
 for ($attempt=0; $attempt -lt 18; $attempt++) {
  try { & (Join-Path $project 'test-endpoints.ps1'); $success=$true; break }
  catch { Start-Sleep -Seconds 5 }
 }
 if (-not $success) { throw 'Echec des endpoints : consulter les fichiers .log.' }
 $registry = Invoke-RestMethod 'http://localhost:8761/eureka/apps' -Headers @{Accept='application/json'}
 $registry | ConvertTo-Json -Depth 12 | Set-Content (Join-Path $project 'verification-eureka.json')
 Write-Output 'VERIFICATION COMPLETE REUSSIE'
} finally {
 foreach ($p in $processes) { Stop-Process -Id $p.Id -ErrorAction SilentlyContinue }
}
