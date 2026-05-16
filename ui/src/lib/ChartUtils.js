export const getCustomTooltip = (valueFormatter) => {
    return function({ series, seriesIndex, dataPointIndex, w }) {
        const hoveredX = w.config.series[seriesIndex].data[dataPointIndex].x;
        const values = w.config.series.map((s, idx) => {
            const dataPoint = s.data.find(d => d.x === hoveredX);
            return {
                name: s.name,
                value: dataPoint ? dataPoint.y : null,
                color: w.globals.colors && w.globals.colors[idx] ? w.globals.colors[idx] : (w.config.colors ? w.config.colors[idx] : undefined)
            };
        }).filter(v => v.value !== null);
        
        values.sort((a, b) => b.value - a.value);
        
        return `<div style='min-width:180px; padding: 10px;'>` + values.map(v =>
            `<div style='display:flex;align-items:center;margin-bottom:2px;'>` +
            (v.color ? `<span style='display:inline-block;width:10px;height:10px;background:${v.color};margin-right:6px;border-radius:50%'></span>` : "") +
            `<span style='font-weight:500'>${v.name}</span>: <span style='margin-left:4px'>${valueFormatter(v.value)}</span>` +
            `</div>`
        ).join("") + `</div>`;
    };
};
