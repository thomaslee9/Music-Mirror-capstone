const puppeteer = require('puppeteer');
const fs = require('fs');

async function replayRecording() {
    const recording = loadRecording();
    if (!recording) {
        console.log('Recording data is null, stopping execution.');
        return;
    }

    const browser = await puppeteer.launch({ headless: true });
    const page = await browser.newPage();
        // Log page console messages
    page.on('console', msg => console.log('PAGE LOG:', msg.text()));

    // Capture errors within the page
    page.on('error', error => {
        console.log('Error happened somewhere in the page:', error);
    });

    // Capture failed network requests
    page.on('requestfailed', request => {
        console.log('Request failed:', request.url(), 'Reason:', request.failure().errorText);
    });
    await replayActions(page, recording);

    await browser.close();
}

function loadRecording() {
    try {
        const data = fs.readFileSync('MM-Pi.json', 'utf8');
        return JSON.parse(data);
    } catch (error) {
        console.error('Failed to load or parse the recording:', error);
        return null; // Return null to handle this gracefully
    }
}

async function replayActions(page, recording) {
    if (!recording || !Array.isArray(recording.steps)) {
        console.error('No steps found or invalid recording:', recording);
        return; // Exit if there are no steps
    }

    for (const step of recording.steps) {
        console.log(step);
        switch (step.type) {
            case 'setViewport':
                await page.setViewport({
                    width: step.width,
                    height: step.height,
                    deviceScaleFactor: step.deviceScaleFactor,
                    isMobile: step.isMobile,
                    hasTouch: step.hasTouch,
                    isLandscape: step.isLandscape
                });
                break;
            case 'navigate':
                console.log('url: ' + step.url);
                await page.goto(step.url, {timeout: 60000});
                break;
            case 'click':
                // Ensure selectors array is combined into a single CSS selector string
                await page.click(step.selectors.flat().join(' '));
                break;
            case 'change':
                // Execute typing using the first selector provided
                await page.type(step.selectors.flat().join(' '), step.value);
                break;
            case 'keyUp':
                // Simulate a keyUp event on the keyboard
                await page.keyboard.up(step.key);
                break;
            // Add more cases as needed for other step types
        }
    }
}

replayRecording();
