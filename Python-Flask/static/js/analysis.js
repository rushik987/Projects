// Load Google Charts library with core and treemap packages
google.charts.load('current', { packages: ['corechart', 'treemap'] });


// Variables to store dataset, columns, selections, and history
let rawData = [];
let columnKeys = [];
let selectedRow = null;
let selectedColumn = null;
let historyStack = [];
const maxUndoSteps = 3;


// Save current table state for undo functionality
function saveHistory() {
    const snapshot = {
        rawData: JSON.parse(JSON.stringify(rawData)),
        columnKeys: [...columnKeys]
    };
    historyStack.push(snapshot);
    if (historyStack.length > maxUndoSteps) {
        historyStack.shift();
    }
}


// Fetch dataset from backend and initialize table/chart
function fetchData() {
    fetch(`/api/file/${fileId}`)
        .then(res => res.json())
        .then(json => {
            columnKeys = json.columns;
            rawData = json.data.map(row => {
                const rowObj = {};
                columnKeys.forEach((key, i) => rowObj[key] = row[i]);
                return rowObj;
            });
            renderTable();
            drawChart();
            populateColumnDropdown();
        });
}


// Build and display the editable data table
function renderTable() {
    const table = document.getElementById('dataTable');
    const thead = table.querySelector('thead');
    const tbody = table.querySelector('tbody');
    thead.innerHTML = '';
    tbody.innerHTML = '';

    const headerRow = document.createElement('tr');
    columnKeys.forEach((key, colIndex) => {
        const th = document.createElement('th');
        th.innerHTML = `${key}<br>`;
        const filterSelect = document.createElement('select');
        filterSelect.className = 'form-select form-select-sm column-filter';
        filterSelect.dataset.colIndex = colIndex;

        const option = document.createElement('option');
        option.value = '';
        option.textContent = 'All';
        filterSelect.appendChild(option);
        filterSelect.addEventListener('click', e => e.stopPropagation());
        th.onclick = () => {
            selectedColumn = colIndex;
            updateSelection();
        };
        th.appendChild(filterSelect);
        headerRow.appendChild(th);
    });
    thead.appendChild(headerRow);

    rawData.forEach((row, rowIndex) => {
        const tr = document.createElement('tr');
        if (rowIndex === selectedRow) tr.classList.add('table-active');
        tr.addEventListener('click', (event) => {
            // Only select row if this is a single click and not an editable double click
            if (event.detail === 1 && !event.target.isContentEditable) {
                selectedRow = rowIndex;
                updateSelection();
            }
        });


        columnKeys.forEach(key => {
            const td = document.createElement('td');
            
td.textContent = row[key];

// DOUBLE CLICK to enable editing
td.addEventListener('dblclick', () => {
    td.contentEditable = true;
    td.focus();
});

// When user exits cell (blur), disable editing and save
td.addEventListener('blur', () => {
    td.contentEditable = false;
    row[key] = td.textContent;

    // Save to server
    fetch(`/api/file/${fileId}`, {
        method: 'PATCH',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            row: rowIndex,
            column: key,
            new_value: td.textContent
        })
    })
    .then(res => res.json())
    .then(data => {
        if (!data.message) {
            console.error('Update failed:', data.error);
        }
    })
    .catch(err => console.error('Error updating cell:', err));
});

// If ENTER is pressed, finalize edit
td.addEventListener('keydown', (e) => {
    if (e.key === 'Enter') {
        e.preventDefault();
        td.blur();
    }
});


            tr.appendChild(td);
        });
        tbody.appendChild(tr);
    });

    populateFilters();
}


// Refresh table to reflect current row/column selection
function updateSelection() {
    renderTable();
}


// Populate dropdown filters for each column in the table
function populateFilters() {
    const filters = document.querySelectorAll('.column-filter');
    filters.forEach(filter => {
        const colIndex = filter.dataset.colIndex;
        const colKey = columnKeys[colIndex];
        const values = [...new Set(rawData.map(row => row[colKey]))].sort();
        filter.innerHTML = `<option value="">All</option>` + values.map(v => `<option value="${v}">${v}</option>`).join('');
        filter.onchange = () => {
            const val = filter.value;
            const filtered = val ? rawData.filter(row => row[colKey] == val) : rawData;
            renderFilteredTable(filtered);
        };
    });
}


// Re-render table rows after filter is applied
function renderFilteredTable(filteredData) {
    const tbody = document.querySelector('#dataTable tbody');
    tbody.innerHTML = '';
    filteredData.forEach((row, rowIndex) => {
        const tr = document.createElement('tr');
        columnKeys.forEach(key => {
            const td = document.createElement('td');
            td.textContent = row[key];
            tr.appendChild(td);
        });
        tbody.appendChild(tr);
    });
}


// Draw selected chart type based on selected column data
function drawChart() {
    const selectedKey = document.getElementById('columnSelect').value;
    const chartType = document.getElementById('chartType').value;
    
    if (chartType === "TreeMap") {
    const chartDiv = document.getElementById('chart_div');
        const treeData = new google.visualization.DataTable();
        treeData.addColumn('string', 'Label');
        treeData.addColumn('string', 'Parent');
        treeData.addColumn('number', 'Value');

        const counts = {};
        rawData.forEach(row => {
            const val = row[selectedKey];
            counts[val] = (counts[val] || 0) + 1;
        });

        const dataRows = [['All', null, 0]];
        for (let key in counts) {
            dataRows.push([key, 'All', counts[key]]);
        }
        treeData.addRows(dataRows);

        const chart = new google.visualization.TreeMap(chartDiv);
        chart.draw(treeData, {
            minColor: '#f0f9e8',
            midColor: '#addd8e',
            maxColor: '#31a354',
            headerHeight: 15,
            fontColor: 'black',
            showScale: true
        });
        return;
    }

    const chartData = [['Label', 'Count']];
    const countMap = {};

    rawData.forEach(row => {
        const label = row[selectedKey];
        countMap[label] = (countMap[label] || 0) + 1;
    });

    for (let key in countMap) {
        chartData.push([key, countMap[key]]);
    }

    const data = google.visualization.arrayToDataTable(chartData);
    const options = { title: `Distribution by ${selectedKey}`, legend: { position: 'right' } };
    const chartDiv = document.getElementById('chart_div');
    const chart = new google.visualization[chartType](chartDiv);
    chart.draw(data, options);
}


// Fill column dropdown for chart selection
function populateColumnDropdown() {
    const columnSelect = document.getElementById('columnSelect');
    columnSelect.innerHTML = '';
    columnKeys.forEach(key => {
        const option = document.createElement('option');
        option.value = key;
        option.textContent = key;
        columnSelect.appendChild(option);
    });
    columnSelect.onchange = drawChart;
    document.getElementById('chartType').onchange = drawChart;
}


// Add new row below selected row or at end of table
document.getElementById('addRowBtn').onclick = () => {
    saveHistory();
    const newRow = {};
    columnKeys.forEach(key => newRow[key] = '');
    if (selectedRow !== null) {
        rawData.splice(selectedRow + 1, 0, newRow);
    } else {
        rawData.push(newRow);
    }
    renderTable();
};


// Remove currently selected row from dataset
document.getElementById('removeRowBtn').onclick = () => {
    if (selectedRow !== null && selectedRow >= 0) {
        saveHistory();
        rawData.splice(selectedRow, 1);
        selectedRow = null;
        renderTable();
    }
};


// Prompt and add a new column next to selected column or at end
document.getElementById('addColBtn').onclick = () => {
    const newCol = prompt('Enter column name:');
    if (newCol) {
        saveHistory();
        const pos = selectedColumn !== null ? selectedColumn + 1 : columnKeys.length;
        columnKeys.splice(pos, 0, newCol);
        rawData.forEach(row => row[newCol] = '');
        renderTable();
    }
};


// Remove selected column from dataset
document.getElementById('removeColBtn').onclick = () => {
    if (selectedColumn !== null) {
        saveHistory();
        const colName = columnKeys[selectedColumn];
        columnKeys.splice(selectedColumn, 1);
        rawData.forEach(row => delete row[colName]);
        selectedColumn = null;
        renderTable();
    }
};


// Revert to the last saved state from history
document.getElementById('undoBtn').onclick = () => {
    if (historyStack.length === 0) {
        alert("Nothing to undo.");
        return;
    }
    const lastState = historyStack.pop();
    rawData = JSON.parse(JSON.stringify(lastState.rawData));
    columnKeys = [...lastState.columnKeys];
    selectedRow = null;
    selectedColumn = null;
    renderTable();
    drawChart();
    populateColumnDropdown();
};


// Start by fetching data once Google Charts is loaded
google.charts.setOnLoadCallback(fetchData);
