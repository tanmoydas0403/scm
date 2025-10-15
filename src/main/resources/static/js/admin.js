//this file name is admin.js
console.log("admin page loaded");

// Wait for the DOM to be fully loaded
document.addEventListener('DOMContentLoaded', function() {
    const imageInput = document.querySelector("#image_file_input");
    const imagePreview = document.querySelector("#upload_image_preview");
    
    if (imageInput && imagePreview) {
        console.log("Image preview functionality initialized");
        
        imageInput.addEventListener('change', function(event) {
            let file = event.target.files[0];
            
            if (file) {
                console.log("File selected:", file.name, "Type:", file.type, "Size:", file.size, "bytes");
                
                // Basic client-side validation
                const maxSize = 2 * 1024 * 1024; // 2MB
                const allowedTypes = ['image/jpeg', 'image/png', 'image/jpg', 'image/gif'];
                
                if (file.size > maxSize) {
                    alert('File size exceeds 2MB limit. Please choose a smaller file.');
                    event.target.value = '';
                    imagePreview.style.display = 'none';
                    return;
                }
                
                if (!allowedTypes.includes(file.type)) {
                    alert('Please select a valid image file (JPEG, PNG, JPG, GIF).');
                    event.target.value = '';
                    imagePreview.style.display = 'none';
                    return;
                }
                
                let reader = new FileReader();
                
                reader.onload = function(event) {
                    imagePreview.setAttribute("src", reader.result);
                    imagePreview.style.display = "block";
                    console.log("Image preview updated");
                }
                
                reader.onerror = function() {
                    console.error("Error reading file");
                    alert('Error reading the selected file. Please try another image.');
                    imagePreview.style.display = "none";
                }
                
                reader.readAsDataURL(file);
            } else {
                // If no file selected, hide the preview
                imagePreview.style.display = "none";
                imagePreview.removeAttribute("src");
                console.log("No file selected, preview hidden");
            }
        });
        
        // Handle form reset to clear preview
        const form = document.querySelector('form');
        if (form) {
            form.addEventListener('reset', function() {
                imagePreview.style.display = "none";
                imagePreview.removeAttribute("src");
                console.log("Form reset, preview cleared");
            });
        }
        
        // Clear file input and preview when navigating away (optional)
        window.addEventListener('beforeunload', function() {
            imageInput.value = '';
            imagePreview.style.display = 'none';
            imagePreview.removeAttribute('src');
        });
        
    } else {
        console.error("Image input or preview element not found");
    }
});


// //this file name is admin.js
// console.log("admin page loaded");

// // Wait for the DOM to be fully loaded
// document.addEventListener('DOMContentLoaded', function() {
//     const imageInput = document.querySelector("#image_file_input");
//     const imagePreview = document.querySelector("#upload_image_preview");
//     const defaultImageUrl = "https://www.shutterstock.com/image-vector/default-avatar-profile-icon-social-600nw-1677509740.jpg";
    
//     if (imageInput && imagePreview) {
//         console.log("Image preview functionality initialized");
        
//         // Set default image initially
//         imagePreview.setAttribute("src", defaultImageUrl);
//         imagePreview.style.display = "block";
//         console.log("Default image set");
        
//         imageInput.addEventListener('change', function(event) {
//             let file = event.target.files[0];
            
//             if (file) {
//                 console.log("File selected:", file.name, "Type:", file.type, "Size:", file.size, "bytes");
                
//                 // Basic client-side validation
//                 const maxSize = 2 * 1024 * 1024; // 2MB
//                 const allowedTypes = ['image/jpeg', 'image/png', 'image/jpg', 'image/gif'];
                
//                 if (file.size > maxSize) {
//                     alert('File size exceeds 2MB limit. Please choose a smaller file.');
//                     event.target.value = '';
//                     // Revert to default image when file is too large
//                     imagePreview.setAttribute("src", defaultImageUrl);
//                     imagePreview.style.display = "block";
//                     return;
//                 }
                
//                 if (!allowedTypes.includes(file.type)) {
//                     alert('Please select a valid image file (JPEG, PNG, JPG, GIF).');
//                     event.target.value = '';
//                     // Revert to default image when file type is invalid
//                     imagePreview.setAttribute("src", defaultImageUrl);
//                     imagePreview.style.display = "block";
//                     return;
//                 }
                
//                 let reader = new FileReader();
                
//                 reader.onload = function(event) {
//                     imagePreview.setAttribute("src", reader.result);
//                     imagePreview.style.display = "block";
//                     console.log("Image preview updated");
//                 }
                
//                 reader.onerror = function() {
//                     console.error("Error reading file");
//                     alert('Error reading the selected file. Please try another image.');
//                     // Revert to default image on read error
//                     imagePreview.setAttribute("src", defaultImageUrl);
//                     imagePreview.style.display = "block";
//                 }
                
//                 reader.readAsDataURL(file);
//             } else {
//                 // If no file selected, show default image
//                 imagePreview.setAttribute("src", defaultImageUrl);
//                 imagePreview.style.display = "block";
//                 console.log("No file selected, default image shown");
//             }
//         });
        
//         // Handle form reset to show default picture
//         const form = document.querySelector('form');
//         if (form) {
//             form.addEventListener('reset', function() {
//                 imagePreview.setAttribute("src", defaultImageUrl);
//                 imagePreview.style.display = "block";
//                 console.log("Form reset, default image shown");
//             });
//         }
        
//         // Clear file input but keep default preview when navigating away
//         window.addEventListener('beforeunload', function() {
//             imageInput.value = '';
//             imagePreview.setAttribute("src", defaultImageUrl);
//             imagePreview.style.display = "block";
//         });
        
//     } else {
//         console.error("Image input or preview element not found");
//     }
// });