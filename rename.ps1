param(
    [string]$newName = $(throw 'specify a new project name!')
)

Get-ChildItem ./* -Recurse |
    Where-Object { $_.Name -match 'mavenfx' } |
    ForEach-Object {
        $newFileName = $_.Name -replace 'mavenfx',$newName
        Write-Host "$($_.FullName) -> $newFileName"
        Rename-Item -Path $_ -NewName $newFileName
    }
Get-ChildItem ./* -Recurse -File -Exclude rename.ps1 | ForEach-Object {
    Select-String 'mavenfx' -Path $_ | Write-Host
    $text = Get-Content $_ -Encoding utf8
    $text = $text -replace 'mavenfx',$newName
    Set-Content $_ $text
}
