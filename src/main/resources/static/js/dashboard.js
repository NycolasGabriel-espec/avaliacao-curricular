'use strict';

const chartPanel = document.querySelector('[data-chart-panel]');
const chartRows = Array.from(document.querySelectorAll('[data-disciplina]'), row => row.dataset);

if (chartPanel && chartRows.length && typeof Chart === 'function') {
    const canvas = chartPanel.querySelector('#comparativo');
    const pagination = chartPanel.querySelector('[data-chart-pagination]');
    const previous = chartPanel.querySelector('[data-chart-prev]');
    const next = chartPanel.querySelector('[data-chart-next]');
    const range = chartPanel.querySelector('[data-chart-range]');
    const palette = getComputedStyle(chartPanel);
    const criteria = [
        {field: 'ementa', label: 'Clareza da ementa', color: '--chart-ementa'},
        {field: 'relevancia', label: 'Relevância do conteúdo', color: '--chart-relevancia'},
        {field: 'carga', label: 'Adequação da carga horária', color: '--chart-carga'},
        {field: 'objetivos', label: 'Cumprimento dos objetivos', color: '--chart-objetivos'}
    ];
    const pageSize = 6;
    const score = new Intl.NumberFormat('pt-BR', {maximumFractionDigits: 2, minimumFractionDigits: 2});
    let currentPage = 0;

    function dataForPage() {
        const start = currentPage * pageSize;
        const page = chartRows.slice(start, start + pageSize);
        return {
            labels: page.map(item => `${item.codigo} · ${item.periodo}`),
            datasets: criteria.map(({field, label, color}) => ({
                label,
                data: page.map(item => Number(item[field])),
                backgroundColor: palette.getPropertyValue(color).trim(),
                borderRadius: 4,
                maxBarThickness: 27
            }))
        };
    }

    function updatePagination() {
        const start = currentPage * pageSize;
        range.textContent = `${start + 1}–${Math.min(start + pageSize, chartRows.length)} de ${chartRows.length}`;
        previous.disabled = currentPage === 0;
        next.disabled = start + pageSize >= chartRows.length;
    }

    chartPanel.hidden = false;
    pagination.hidden = chartRows.length <= pageSize;

    const chart = new Chart(canvas, {
        type: 'bar',
        data: dataForPage(),
        options: {
            responsive: true,
            maintainAspectRatio: false,
            locale: 'pt-BR',
            animation: false,
            scales: {
                x: {
                    ticks: {color: '#4d5c72', maxRotation: 0, autoSkip: false},
                    grid: {display: false}
                },
                y: {
                    min: 0,
                    max: 5,
                    ticks: {color: '#4d5c72', stepSize: 1},
                    grid: {color: '#e3e9f3'}
                }
            },
            plugins: {
                legend: {display: false},
                tooltip: {
                    callbacks: {
                        title: items => chartRows[currentPage * pageSize + items[0].dataIndex].disciplina,
                        label: item => `${item.dataset.label} · ${score.format(item.parsed.y)}`
                    }
                }
            }
        }
    });

    function changePage(offset) {
        currentPage += offset;
        chart.data = dataForPage();
        chart.update();
        updatePagination();
    }

    previous.addEventListener('click', () => changePage(-1));
    next.addEventListener('click', () => changePage(1));
    updatePagination();
}
