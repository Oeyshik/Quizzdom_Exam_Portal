# Assets Folder

This folder contains all static assets for the Exam Portal application.

## Folder Structure

- **logos/**: Contains logo files
  - `logo 19.png` - Main application logo
  - `logo 19.ico` - Favicon icon

- **images/**: Contains image files
  - `bg-texture.png` - Background texture image
  - `home-image.png` - Home page image (needs to be downloaded)
  - `signup-image-1.png` - Signup page image 1 (needs to be downloaded)
  - `signup-image-2.png` - Signup page image 2 (needs to be downloaded)

## Images to Download

The following images need to be downloaded from Google Drive and placed in the `images/` folder:

1. **home-image.png** (for home.component.html)
   - Google Drive ID: `1iuSmjlL3WAxtebAc0XYNWbgeW6m-gJDe`
   - Direct link: `https://drive.google.com/uc?id=1iuSmjlL3WAxtebAc0XYNWbgeW6m-gJDe`
   - Save as: `images/home-image.png`

2. **signup-image-1.png** (for signup.component.html - currently commented out)
   - Google Drive ID: `1iuSmjlL3WAxtebAc0XYNWbgeW6m-gJDe`
   - Direct link: `https://drive.google.com/uc?id=1iuSmjlL3WAxtebAc0XYNWbgeW6m-gJDe`
   - Save as: `images/signup-image-1.png`

3. **signup-image-2.png** (for signup.component.html - currently commented out)
   - Google Drive ID: `18TZkbCG4ELx6iTJES6yRLT2cPC7vTu3R`
   - Direct link: `https://drive.google.com/uc?id=18TZkbCG4ELx6iTJES6yRLT2cPC7vTu3R`
   - Save as: `images/signup-image-2.png`

## Usage in Components

All assets should be referenced using the Angular assets path:
- Logos: `assets/logos/logo 19.png`
- Images: `assets/images/image-name.png`

Example:
```html
<img src="assets/logos/logo 19.png" alt="Logo" />
<img src="assets/images/home-image.png" alt="Home Image" />
```

