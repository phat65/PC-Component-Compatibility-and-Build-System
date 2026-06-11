// suggest.js - Handle build suggestion functionality
(function() {
    'use strict';

    // State management
    let state = {
        selectedPreset: null,
        presets: [
            {
                id: 'GAMING_HIGH',
                name: 'High-End Gaming',
                description: 'Top performance for 4K gaming',
                minBudget: 2000,
                icon: '4K'
            },
            {
                id: 'GAMING_MID',
                name: 'Mid-Range Gaming',
                description: 'Great 1440p gaming experience',
                minBudget: 1200,
                icon: '1440p'
            },
            {
                id: 'BUDGET_GAMING',
                name: 'Budget Gaming',
                description: 'Solid 1080p gaming',
                minBudget: 700,
                icon: '1080p'
            },
            {
                id: 'WORKSTATION',
                name: 'Workstation',
                description: 'For content creation & 3D work',
                minBudget: 1500,
                icon: 'Work'
            },
            {
                id: 'STREAMING',
                name: 'Streaming PC',
                description: 'For gaming & streaming',
                minBudget: 1400,
                icon: 'Stream'
            },
            {
                id: 'OFFICE',
                name: 'Office PC',
                description: 'For productivity work',
                minBudget: 500,
                icon: 'Office'
            }
        ],
        suggestedBuild: null
    };
    function getCsrfToken() {
        const csrfMeta = document.querySelector('meta[name="_csrf"]');
        const metaToken = csrfMeta ? csrfMeta.getAttribute('content') : '';
        if (metaToken) {
            return metaToken;
        }

        const match = document.cookie.match(/(?:^|; )XSRF-TOKEN=([^;]+)/);
        return match ? decodeURIComponent(match[1]) : '';
    }

    function getCsrfHeader() {
        const csrfHeaderMeta = document.querySelector('meta[name="_csrf_header"]');
        return csrfHeaderMeta ? csrfHeaderMeta.getAttribute('content') : 'X-XSRF-TOKEN';
    }

    function withCsrfHeaders(headers) {
        const csrfToken = getCsrfToken();
        if (!csrfToken) {
            return headers;
        }

        return {
            ...headers,
            [getCsrfHeader()]: csrfToken
        };
    }

    function escapeHtml(value) {
        return String(value ?? '')
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;')
            .replace(/'/g, '&#039;');
    }

    // DOM Elements
    const elements = {
        // Buttons
        btnShowSuggest: document.getElementById('btnShowSuggest'),
        btnGenerateSuggest: document.getElementById('btnGenerateSuggest'),
        btnApplySuggest: document.getElementById('btnApplySuggest'),
        btnResetSuggest: document.getElementById('btnResetSuggest'),

        // Sections
        welcomeMessage: document.getElementById('welcomeMessage'),
        suggestForm: document.getElementById('suggestForm'),
        budgetStep: document.getElementById('budgetStep'),
        resultStep: document.getElementById('resultStep'),
        errorMessage: document.getElementById('errorMessage'),

        // Inputs
        presetList: document.getElementById('presetList'),
        budgetInput: document.getElementById('budgetInput'),
        budgetHint: document.getElementById('budgetHint'),
        resultContent: document.getElementById('resultContent')
    };

    // Initialize
    function init() {
        attachEventListeners();
    }

    // Attach event listeners
    function attachEventListeners() {
        if (elements.btnShowSuggest) {
            elements.btnShowSuggest.addEventListener('click', showSuggestForm);
        }

        elements.btnGenerateSuggest?.addEventListener('click', generateSuggestion);
        elements.btnApplySuggest?.addEventListener('click', applySuggestedBuild);
        elements.btnResetSuggest?.addEventListener('click', resetSuggestForm);
        elements.budgetInput?.addEventListener('input', handleBudgetInput);
    }

    // Show suggest form in sidebar
    function showSuggestForm() {
        if (!elements.welcomeMessage || !elements.suggestForm) {
            return;
        }

        elements.welcomeMessage.style.display = 'none';
        elements.suggestForm.style.display = 'block';
        elements.errorMessage.style.display = 'none';

        renderPresets();
    }

    // Render presets in sidebar
    function renderPresets() {
        const presetsHtml = state.presets.map(preset => `
            <div class="preset-item" data-preset="${preset.id}" data-min-budget="${preset.minBudget}">
                <div class="preset-icon">${escapeHtml(preset.icon)}</div>
                <div class="preset-info">
                    <div class="preset-name">${escapeHtml(preset.name)}</div>
                    <div class="preset-budget">${escapeHtml(preset.description)}</div>
                    <div class="preset-budget">Min: $${preset.minBudget}</div>
                </div>
            </div>
        `).join('');

        elements.presetList.innerHTML = presetsHtml;

        // Attach click handlers to preset items
        document.querySelectorAll('.preset-item').forEach(item => {
            item.addEventListener('click', () => selectPreset(item));
        });
    }

    // Select preset
    function selectPreset(item) {
        // Deselect all items
        document.querySelectorAll('.preset-item').forEach(i => {
            i.classList.remove('selected');
        });

        // Select this item
        item.classList.add('selected');

        const presetId = item.dataset.preset;
        const minBudget = parseFloat(item.dataset.minBudget);

        state.selectedPreset = state.presets.find(p => p.id === presetId);

        // Show budget section
        elements.budgetStep.style.display = 'block';
        elements.budgetInput.min = minBudget;
        elements.budgetInput.placeholder = `Min: $${minBudget}`;
        elements.budgetHint.textContent = `Minimum: $${minBudget}`;
        elements.budgetHint.style.color = '#666';

        // Reset result and error
        elements.resultStep.style.display = 'none';
        elements.errorMessage.style.display = 'none';

        // Check if budget is already entered
        handleBudgetInput();
    }

    // Handle budget input
    function handleBudgetInput() {
        if (!state.selectedPreset) return;

        const budget = parseFloat(elements.budgetInput.value);
        const minBudget = state.selectedPreset.minBudget;

        if (!budget || budget < minBudget) {
            elements.btnGenerateSuggest.disabled = true;
            elements.budgetHint.style.color = '#e74c3c';
            elements.budgetHint.textContent = `Minimum: $${minBudget}`;
        } else {
            elements.btnGenerateSuggest.disabled = false;
            elements.budgetHint.style.color = '#27ae60';
            elements.budgetHint.textContent = `Budget: $${budget}`;
        }
    }

    // Generate suggestion
    async function generateSuggestion() {
        if (!state.selectedPreset) {
            showError('Please select a preset first.');
            return;
        }

        const budget = parseFloat(elements.budgetInput.value);
        if (!budget || budget < state.selectedPreset.minBudget) {
            showError(`Budget must be at least $${state.selectedPreset.minBudget}`);
            return;
        }

        try {
            const btnText = elements.btnGenerateSuggest.querySelector('.btn-text');
            const btnLoading = elements.btnGenerateSuggest.querySelector('.btn-loading');
            btnText.style.display = 'none';
            btnLoading.style.display = 'inline';
            elements.btnGenerateSuggest.disabled = true;
            elements.resultStep.style.display = 'block';
            elements.resultContent.innerHTML = '<div class="suggest-state loading">Generating compatible build...</div>';
            elements.errorMessage.style.display = 'none';

            const response = await fetch('/api/build/suggest', {
                method: 'POST',
                headers: withCsrfHeaders({
                    'Content-Type': 'application/json'
                }),
                body: JSON.stringify({
                    preset: state.selectedPreset.id,
                    budget: budget
                })
            });

            if (response.status === 403) {
                throw new Error('PC Builder is available for customers and guests. Please switch from admin/staff account.');
            }

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(errorText || 'Failed to generate suggestion');
            }

            state.suggestedBuild = await response.json();
            renderSuggestedBuild();

        } catch (error) {
            console.error('Error generating suggestion:', error);
            showError(error.message || 'Failed to generate build suggestion. Please try again.');
        } finally {
            const btnText = elements.btnGenerateSuggest.querySelector('.btn-text');
            const btnLoading = elements.btnGenerateSuggest.querySelector('.btn-loading');
            btnText.style.display = 'inline';
            btnLoading.style.display = 'none';
            elements.btnGenerateSuggest.disabled = false;
        }
    }

    // Render suggested build
    function renderSuggestedBuild() {
        if (!state.suggestedBuild) return;

        const build = state.suggestedBuild;
        let totalPrice = 0;

        const componentNames = {
            mainboard: 'Mainboard',
            cpu: 'CPU',
            memory: 'Memory',
            gpu: 'GPU',
            storage: 'Storage',
            powerSupply: 'PSU',
            pcCase: 'Case',
            cooling: 'Cooling'
        };

        let buildHtml = '<div class="build-list">';
        let componentCount = 0;

        Object.keys(componentNames).forEach(key => {
            const component = build[key];
            if (component && component.productId) {
                totalPrice += component.price || 0;
                componentCount += 1;

                buildHtml += `
                    <div class="build-item">
                        <div class="build-label">${escapeHtml(componentNames[key])}</div>
                        <div class="build-name">${escapeHtml(component.productName || 'N/A')}</div>
                        <div class="build-price">$${(component.price || 0).toFixed(2)}</div>
                    </div>
                `;
            }
        });

        if (componentCount === 0) {
            elements.resultContent.innerHTML = '<div class="suggest-state">No compatible build was returned for this budget.</div>';
            elements.resultStep.style.display = 'block';
            elements.errorMessage.style.display = 'none';
            elements.btnApplySuggest.disabled = true;
            return;
        }

        buildHtml += `
            <div class="build-total">
                <strong>Total: $${totalPrice.toFixed(2)}</strong>
            </div>
        </div>`;

        elements.resultContent.innerHTML = buildHtml;
        elements.resultStep.style.display = 'block';
        elements.errorMessage.style.display = 'none';
        elements.btnApplySuggest.disabled = false;
    }

    // Apply suggested build
    function applySuggestedBuild() {
        if (!state.suggestedBuild) {
            showError('No build to apply.');
            return;
        }

        // Call API to convert and store in session
        fetch('/api/build/apply', {
            method: 'POST',
            headers: withCsrfHeaders({
                    'Content-Type': 'application/json'
                }),
            body: JSON.stringify(state.suggestedBuild)
        })
        .then(response => {
            if (response.status === 403) {
                throw new Error('PC Builder is available for customers and guests. Please switch from admin/staff account.');
            }
            if (!response.ok) {
                throw new Error('Failed to apply build');
            }
            return response.json();
        })
        .then(data => {
            // Redirect to mainboard page to show the build
            window.location.href = '/build/mainboard?applied=true';
        })
        .catch(error => {
            console.error('Error applying build:', error);
            showError(error.message || 'Failed to apply build. Please try again.');
        });
    }

    // Reset suggest form
    function resetSuggestForm() {
        state.selectedPreset = null;
        state.suggestedBuild = null;

        elements.budgetStep.style.display = 'none';
        elements.resultStep.style.display = 'none';
        elements.errorMessage.style.display = 'none';
        elements.resultContent.innerHTML = '';
        elements.budgetInput.value = '';
        elements.budgetHint.textContent = '';
        elements.btnGenerateSuggest.disabled = true;
        elements.btnApplySuggest.disabled = false;

        // Deselect all preset items
        document.querySelectorAll('.preset-item').forEach(item => {
            item.classList.remove('selected');
        });

        // Scroll back to top
        elements.suggestForm.scrollTop = 0;
    }

    // Show error
    function showError(message) {
        elements.errorMessage.style.display = 'block';
        elements.errorMessage.textContent = 'Warning: ' + message;
        elements.resultStep.style.display = 'none';
        elements.resultContent.innerHTML = '';

        setTimeout(() => {
            elements.errorMessage.style.display = 'none';
        }, 5000);
    }

    // Initialize when DOM is ready
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }
})();


