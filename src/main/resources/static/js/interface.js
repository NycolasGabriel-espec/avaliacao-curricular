'use strict';

document.querySelector('[data-focus-errors]')?.focus();

const evaluationForm = document.querySelector('[data-evaluation-form]');
const evaluationProgress = document.querySelector('[data-evaluation-progress]');
const disciplineSelect = evaluationForm?.querySelector('#disciplinaId');

if (evaluationForm && evaluationProgress && disciplineSelect) {
    const progressBar = evaluationProgress.querySelector('progress');
    const progressLabel = evaluationProgress.querySelector('[data-progress-label]');

    function updateProgress() {
        const ratings = evaluationForm.querySelectorAll('input[type="radio"]:checked').length;
        const completed = ratings + Number(disciplineSelect.value !== '');
        progressBar.value = completed;
        progressLabel.textContent = `${completed} de ${progressBar.max} campos`;
    }

    evaluationForm.addEventListener('change', updateProgress);
    window.addEventListener('pageshow', updateProgress);
    updateProgress();
    evaluationProgress.hidden = false;
}
