Add-Type -AssemblyName System.Drawing

$files = @(
    "portal_ship.png",
    "portal_cube.png",
    "portal_normal_gravity.png", 
    "portal_reverse_gravity.png",
    "portal_1x_speed.png",
    "portal_4x_speed.png"
)

$basePath = "d:\Downloads\Materi\Semester 3\OOP\Prak\EinsteinDash\Frontend\assets\portal"

foreach ($file in $files) {
    $path = Join-Path $basePath $file
    if (Test-Path $path) {
        $img = [System.Drawing.Image]::FromFile($path)
        Write-Host "$file : $($img.Width) x $($img.Height)"
        $img.Dispose()
    } else {
        Write-Host "$file : NOT FOUND"
    }
}
