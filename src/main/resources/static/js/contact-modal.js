// contact-modal.js - Contact Modal functionality

// Delete Modal Variables
let currentDeleteContactId = null;
let currentDeleteContactName = null;

// Edit Modal Variables
let currentEditContactId = null;

// Function to open contact modal and fetch details
async function viewContact(contactId) {
    try {
        // Clean the contactId by removing any surrounding quotes
        const cleanContactId = contactId.replace(/^"|"$/g, '');
        console.log('Fetching contact details for:', cleanContactId);
        
        // Show loading state
        showLoadingState();
        
        // Fetch contact details from API
        const response = await fetch(`/api/contact/${cleanContactId}`);
        
        if (!response.ok) {
            throw new Error(`Failed to fetch contact details. Status: ${response.status}`);
        }
        
        const contact = await response.json();
        console.log('Contact data received:', contact);
        
        // Populate modal with contact data
        populateContactModal(contact);
        
        // Show the modal
        openContactModal();
        
    } catch (error) {
        console.error('Error fetching contact:', error);
        showNotification('Failed to load contact details. Please try again.', 'error');
    } finally {
        hideLoadingState();
    }
}

// Function to populate modal with contact data
function populateContactModal(contact) {
    // Basic information
    document.getElementById('contact_name').textContent = contact.name || 'N/A';
    document.getElementById('contact_name_mobile').textContent = contact.name || 'N/A';
    document.getElementById('contact_phone').textContent = contact.phoneNumber || 'N/A';
    document.getElementById('contact_email').textContent = contact.email || 'N/A';
    document.getElementById('contact_address').textContent = contact.address || 'No address provided';
    document.getElementById('contact_about').textContent = contact.description || 'No description provided';
    
    // Contact image - larger and better visible
    const contactImage = document.getElementById('contact_image');
    contactImage.src = contact.picture || 'https://www.shutterstock.com/image-vector/default-avatar-profile-icon-social-600nw-1677509740.jpg';
    contactImage.alt = `Picture of ${contact.name}`;
    
    // Add error handling for broken images
    contactImage.onerror = function() {
        this.src = 'https://www.shutterstock.com/image-vector/default-avatar-profile-icon-social-600nw-1677509740.jpg';
    };
    
    // Favorite status - both in header and on image
    const favoriteElement = document.getElementById('contact_favorite');
    const favoriteMobileElement = document.getElementById('contact_favorite_mobile');
    const imageFavoriteBadge = document.getElementById('image_favorite_badge');
    
    if (contact.favorite) {
        favoriteElement.innerHTML = '<i class="fas fa-star text-yellow-500 text-xl" title="Favorite Contact"></i>';
        favoriteMobileElement.innerHTML = '<i class="fas fa-star text-yellow-500 text-lg" title="Favorite Contact"></i>';
        imageFavoriteBadge.classList.remove('hidden');
    } else {
        favoriteElement.innerHTML = '';
        favoriteMobileElement.innerHTML = '';
        imageFavoriteBadge.classList.add('hidden');
    }
    
    // Website link
    const websiteContainer = document.getElementById('contact_website_container');
    const websiteLink = document.getElementById('contact_website');
    if (contact.websiteLink && contact.websiteLink.trim() !== '') {
        websiteContainer.classList.remove('hidden');
        let websiteUrl = contact.websiteLink;
        if (!websiteUrl.startsWith('http://') && !websiteUrl.startsWith('https://')) {
            websiteUrl = 'https://' + websiteUrl;
        }
        websiteLink.href = websiteUrl;
    } else {
        websiteContainer.classList.add('hidden');
    }
    
    // LinkedIn link
    const linkedInContainer = document.getElementById('contact_linkedIn_container');
    const linkedInLink = document.getElementById('contact_linkedIn');
    if (contact.linkedInLink && contact.linkedInLink.trim() !== '') {
        linkedInContainer.classList.remove('hidden');
        let linkedInUrl = contact.linkedInLink;
        if (!linkedInUrl.startsWith('http://') && !linkedInUrl.startsWith('https://')) {
            linkedInUrl = 'https://' + linkedInUrl;
        }
        linkedInLink.href = linkedInUrl;
    } else {
        linkedInContainer.classList.add('hidden');
    }
    
    // Show/hide address and about sections based on content
    const hasAddress = contact.address && contact.address.trim() !== '';
    const hasDescription = contact.description && contact.description.trim() !== '';
    
    document.getElementById('contact_address_container').classList.toggle('hidden', !hasAddress);
    document.getElementById('contact_about_container').classList.toggle('hidden', !hasDescription);
}

// Function to open the modal
function openContactModal() {
    const modal = document.getElementById('view_contact_modal');
    if (modal) {
        modal.classList.remove('hidden');
        modal.classList.add('flex');
        document.body.classList.add('overflow-hidden');
        document.documentElement.classList.add('overflow-hidden');
    }
}

// Function to close the modal
function closeContactModal() {
    const modal = document.getElementById('view_contact_modal');
    if (modal) {
        modal.classList.add('hidden');
        modal.classList.remove('flex');
        document.body.classList.remove('overflow-hidden');
        document.documentElement.classList.remove('overflow-hidden');
    }
}

// ========== EDIT FUNCTIONALITY ==========

// Function to open edit modal and fetch contact data
async function openEditModal(contactId) {
    try {
        currentEditContactId = contactId;
        
        // Clean the contactId by removing any surrounding quotes
        const cleanContactId = contactId.replace(/^"|"$/g, '');
        console.log('Fetching contact for edit:', cleanContactId);
        
        // Show loading state
        showEditLoadingState();
        
        // Fetch contact details from API
        const response = await fetch(`/api/contact/${cleanContactId}`);
        
        if (!response.ok) {
            throw new Error(`Failed to fetch contact details. Status: ${response.status}`);
        }
        
        const contact = await response.json();
        console.log('Contact data for edit received:', contact);
        
        // Populate edit form with contact data
        populateEditForm(contact);
        
        // Show the modal
        showEditModal();
        
    } catch (error) {
        console.error('Error fetching contact for edit:', error);
        showNotification('Failed to load contact for editing. Please try again.', 'error');
    } finally {
        hideEditLoadingState();
    }
}

// Function to populate edit form with contact data
function populateEditForm(contact) {
    // Basic information
    document.getElementById('edit_contact_id').value = contact.id;
    document.getElementById('edit_name').value = contact.name || '';
    document.getElementById('edit_email').value = contact.email || '';
    document.getElementById('edit_phone').value = contact.phoneNumber || '';
    document.getElementById('edit_address').value = contact.address || '';
    document.getElementById('edit_description').value = contact.description || '';
    document.getElementById('edit_website').value = contact.websiteLink || '';
    document.getElementById('edit_linkedin').value = contact.linkedInLink || '';
    document.getElementById('edit_favorite').checked = contact.favorite || false;
    
    // Profile picture preview
    const picturePreview = document.getElementById('edit_picture_preview');
    picturePreview.src = contact.picture || 'https://www.shutterstock.com/image-vector/default-avatar-profile-icon-social-600nw-1677509740.jpg';
}

// Function to show edit modal
function showEditModal() {
    const modal = document.getElementById('edit_contact_modal');
    if (modal) {
        modal.classList.remove('hidden');
        modal.classList.add('flex');
        document.body.classList.add('overflow-hidden');
        document.documentElement.classList.add('overflow-hidden');
    }
}

// Function to close edit modal
function closeEditModal() {
    const modal = document.getElementById('edit_contact_modal');
    if (modal) {
        modal.classList.add('hidden');
        modal.classList.remove('flex');
        document.body.classList.remove('overflow-hidden');
        document.documentElement.classList.remove('overflow-hidden');
        currentEditContactId = null;
        
        // Reset form
        document.getElementById('editContactForm').reset();
        document.getElementById('edit_picture_preview').src = 'https://www.shutterstock.com/image-vector/default-avatar-profile-icon-social-600nw-1677509740.jpg';
    }
}

// Function to preview image in edit form
function previewEditImage(input) {
    const preview = document.getElementById('edit_picture_preview');
    if (input.files && input.files[0]) {
        const reader = new FileReader();
        reader.onload = function(e) {
            preview.src = e.target.result;
        };
        reader.readAsDataURL(input.files[0]);
    }
}

// Function to submit edit form
async function submitEditForm() {
    try {
        const form = document.getElementById('editContactForm');
        const formData = new FormData(form);
        
        // Get CSRF token if available
        const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
        const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content') || 'X-CSRF-TOKEN';
        
        const headers = {};
        
        // Add CSRF token if available
        if (csrfToken) {
            headers[csrfHeader] = csrfToken;
        }
        
        // Note: You'll need to create an update endpoint in your controller
        const response = await fetch(`/user/contacts/update/${currentEditContactId}`, {
            method: 'POST',
            headers: headers,
            body: formData,
            credentials: 'include'
        });
        
        if (response.ok) {
            showNotification('Contact updated successfully!', 'success');
            closeEditModal();
            
            // Reload the page to reflect changes
            setTimeout(() => {
                window.location.reload();
            }, 1000);
            
        } else {
            const errorText = await response.text();
            console.error('Update failed with status:', response.status, 'Error:', errorText);
            showNotification('Failed to update contact. Please try again.', 'error');
        }
    } catch (error) {
        console.error('Error updating contact:', error);
        showNotification('Network error. Please check your connection and try again.', 'error');
    }
}

// ========== DELETE FUNCTIONALITY ==========

// Open delete confirmation modal
function openDeleteModal(contactId, contactName) {
    currentDeleteContactId = contactId;
    currentDeleteContactName = contactName;
    
    // Populate modal with contact info
    document.getElementById('delete_contact_info').innerHTML = `
        <div class="bg-red-50 dark:bg-red-900/20 p-4 rounded-lg border border-red-200 dark:border-red-800">
            <p class="text-red-800 dark:text-red-300 font-semibold text-lg mb-2">You are about to delete:</p>
            <p class="text-red-700 dark:text-red-400 text-xl font-bold">"${contactName}"</p>
            <p class="text-red-600 dark:text-red-500 text-sm mt-3">⚠️ This action cannot be undone!</p>
        </div>
    `;
    
    // Show the modal
    const modal = document.getElementById('delete_contact_modal');
    modal.classList.remove('hidden');
    modal.classList.add('flex');
    document.body.classList.add('overflow-hidden');
}

// Close delete confirmation modal
function closeDeleteModal() {
    const modal = document.getElementById('delete_contact_modal');
    modal.classList.add('hidden');
    modal.classList.remove('flex');
    document.body.classList.remove('overflow-hidden');
    currentDeleteContactId = null;
    currentDeleteContactName = null;
}

// Confirm and execute deletion using API
async function confirmDelete() {
    if (!currentDeleteContactId) {
        console.log('No contact ID to delete');
        return;
    }
    
    try {
        console.log('Starting delete process for:', currentDeleteContactId);
        
        // Get CSRF token if available
        const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
        const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content') || 'X-CSRF-TOKEN';
        
        const headers = {
            'Content-Type': 'application/json',
        };
        
        // Add CSRF token if available
        if (csrfToken) {
            headers[csrfHeader] = csrfToken;
        }
        
        const deleteUrl = `/user/contacts/delete/${currentDeleteContactId}`;
        console.log('Sending DELETE request to:', deleteUrl);
        
        const response = await fetch(deleteUrl, {
            method: 'DELETE',
            headers: headers,
            credentials: 'include'
        });
        
        console.log('Response received, status:', response.status);
        
        if (response.ok) {
            const result = await response.text();
            console.log('Delete successful:', result);
            
            // Close modal first
            closeDeleteModal();
            
            // Show success message
            showNotification('Contact deleted successfully!', 'success');
            
            // Reload the page to reflect changes after a short delay
            setTimeout(() => {
                window.location.reload();
            }, 1000);
            
        } else {
            const errorText = await response.text();
            console.error('Delete failed with status:', response.status, 'Error:', errorText);
            
            let errorMessage = 'Failed to delete contact. Please try again.';
            if (response.status === 404) {
                errorMessage = 'Contact not found. It may have been already deleted.';
            } else if (response.status === 403) {
                errorMessage = 'Permission denied. Please check if you are logged in.';
            } else if (response.status === 500) {
                errorMessage = 'Server error. Please try again later.';
            }
            
            showNotification(errorMessage, 'error');
        }
    } catch (error) {
        console.error('Network error deleting contact:', error);
        showNotification('Network error. Please check your connection and try again.', 'error');
    }
}

// Helper function for notifications
function showNotification(message, type = 'info') {
    // Simple alert-based notification
    if (type === 'success') {
        alert('✅ ' + message);
    } else if (type === 'error') {
        alert('❌ ' + message);
    } else {
        alert('ℹ️ ' + message);
    }
}

// Loading state functions
function showLoadingState() {
    console.log('Loading contact details...');
}

function hideLoadingState() {
    console.log('Loading complete');
}

// Loading state functions for edit modal
function showEditLoadingState() {
    console.log('Loading contact for edit...');
}

function hideEditLoadingState() {
    console.log('Edit loading complete');
}

// ========== EVENT LISTENERS ==========

document.addEventListener('DOMContentLoaded', function() {
    console.log('Contact modal JavaScript initializing...');
    
    // Event delegation for all buttons
    document.addEventListener('click', function(event) {
        // View contact buttons
        if (event.target.closest('.view-contact-btn')) {
            const button = event.target.closest('.view-contact-btn');
            const contactId = button.getAttribute('data-contact-id');
            if (contactId) {
                viewContact(contactId);
            } else {
                console.error('No contact ID found on view button');
            }
        }
        
        // Edit contact buttons
        if (event.target.closest('.edit-contact-btn')) {
            const button = event.target.closest('.edit-contact-btn');
            const contactId = button.getAttribute('data-contact-id');
            if (contactId) {
                event.preventDefault(); // Prevent default link behavior
                openEditModal(contactId);
            } else {
                console.error('No contact ID found on edit button');
            }
        }
        
        // Delete contact buttons
        if (event.target.closest('.delete-contact-btn')) {
            const button = event.target.closest('.delete-contact-btn');
            const contactId = button.getAttribute('data-contact-id');
            const contactName = button.getAttribute('data-contact-name');
            
            if (contactId && contactName) {
                openDeleteModal(contactId, contactName);
            } else {
                console.error('Missing contact ID or name on delete button');
            }
        }
    });
    
    // Modal close event listeners for all modals
    const viewModal = document.getElementById('view_contact_modal');
    const editModal = document.getElementById('edit_contact_modal');
    const deleteModal = document.getElementById('delete_contact_modal');
    
    // View modal events
    if (viewModal) {
        viewModal.addEventListener('click', function(event) {
            if (event.target === viewModal) {
                closeContactModal();
            }
        });
    }
    
    // Edit modal events
    if (editModal) {
        editModal.addEventListener('click', function(event) {
            if (event.target === editModal) {
                closeEditModal();
            }
        });
    }
    
    // Delete modal events
    if (deleteModal) {
        deleteModal.addEventListener('click', function(event) {
            if (event.target === deleteModal) {
                closeDeleteModal();
            }
        });
    }
    
    // Escape key listener for all modals
    document.addEventListener('keydown', function(event) {
        if (event.key === 'Escape') {
            if (viewModal && !viewModal.classList.contains('hidden')) {
                closeContactModal();
            }
            if (editModal && !editModal.classList.contains('hidden')) {
                closeEditModal();
            }
            if (deleteModal && !deleteModal.classList.contains('hidden')) {
                closeDeleteModal();
            }
        }
    });
    
    console.log('Contact modal JavaScript loaded successfully');
});

// Make functions globally available
window.viewContact = viewContact;
window.closeContactModal = closeContactModal;
window.openContactModal = openContactModal;
window.openEditModal = openEditModal;
window.closeEditModal = closeEditModal;
window.previewEditImage = previewEditImage;
window.submitEditForm = submitEditForm;
window.openDeleteModal = openDeleteModal;
window.closeDeleteModal = closeDeleteModal;
window.confirmDelete = confirmDelete;