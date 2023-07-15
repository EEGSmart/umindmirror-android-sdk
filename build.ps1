$builds = Get-ChildItem -Path .\ -Recurse build
for ($i = 0; $i -lt $builds.Length; $i = $i+1){
    $full = $builds[$i].FullName
    Write-Host $full
    Remove-Item -Recurse $full
}