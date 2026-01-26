# PowerShell script to download images from Google Drive
# Run this script from the assets folder: .\download-images.ps1

$imagesFolder = "images"

# Create images folder if it doesn't exist
if (-not (Test-Path $imagesFolder)) {
    New-Item -ItemType Directory -Path $imagesFolder
    Write-Host "Created $imagesFolder folder"
}

# Function to download from Google Drive
function Download-GoogleDriveImage {
    param(
        [string]$FileId,
        [string]$OutputPath
    )
    
    $url = "https://drive.google.com/uc?id=$FileId&export=download"
    
    try {
        Write-Host "Downloading $OutputPath..."
        Invoke-WebRequest -Uri $url -OutFile $OutputPath -UseBasicParsing
        Write-Host "✓ Successfully downloaded $OutputPath" -ForegroundColor Green
    }
    catch {
        Write-Host "✗ Failed to download $OutputPath" -ForegroundColor Red
        Write-Host "Error: $_" -ForegroundColor Red
    }
}

# Download images
Write-Host "Starting image downloads..." -ForegroundColor Cyan
Write-Host ""

# Home page image
Download-GoogleDriveImage -FileId "1iuSmjlL3WAxtebAc0XYNWbgeW6m-gJDe" -OutputPath "$imagesFolder\home-image.png"

# Signup page images
Download-GoogleDriveImage -FileId "1iuSmjlL3WAxtebAc0XYNWbgeW6m-gJDe" -OutputPath "$imagesFolder\signup-image-1.png"
Download-GoogleDriveImage -FileId "18TZkbCG4ELx6iTJES6yRLT2cPC7vTu3R" -OutputPath "$imagesFolder\signup-image-2.png"

Write-Host ""
Write-Host "Download process completed!" -ForegroundColor Cyan
Write-Host "Please verify the downloaded images in the $imagesFolder folder."

