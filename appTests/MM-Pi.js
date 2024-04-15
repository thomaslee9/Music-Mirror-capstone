import url from 'url';
import { createRunner } from '@puppeteer/replay';

export async function run(extension) {
    const runner = await createRunner(extension);

    await runner.runBeforeAllSteps();

    await runner.runStep({
        type: 'setViewport',
        width: 821,
        height: 834,
        deviceScaleFactor: 1,
        isMobile: false,
        hasTouch: false,
        isLandscape: false
    });
    console.log('before Recording');
    await runner.runStep({
        type: 'navigate',
        url: 'http://172.26.54.24:8080/',
        assertedEvents: [
            {
                type: 'navigation',
                url: 'http://172.26.54.24:8080/',
                title: 'Music Mirror app'
            }
        ]
    });
    console.log('Recording started');
    await runner.runStep({
        type: 'click',
        target: 'main',
        selectors: [
            [
                '#name'
            ]
        ],
        offsetY: 29.0625,
        offsetX: 190.3333282470703,
    });
    await runner.runStep({
        type: 'change',
        value: 'U',
        selectors: [
            [
                '#name'
            ]
        ],
        target: 'main'
    });
    await runner.runStep({
        type: 'keyUp',
        key: 'u',
        target: 'main'
    });
    await runner.runStep({
        type: 'change',
        value: 'User',
        selectors: [
            [
                '#name'
            ]
        ],
        target: 'main'
    });
    await runner.runStep({
        type: 'click',
        target: 'main',
        selectors: [
            [
                '#username-page button'
            ],
            [
                'text/Submit'
            ]
        ],
        offsetY: 14.0625,
        offsetX: 30.791656494140625,
    });
    await runner.runStep({
        type: 'click',
        target: 'main',
        selectors: [
            [
                '#reqName'
            ]
        ],
        offsetY: 10.08331298828125,
        offsetX: 243.3333282470703,
    });
    await runner.runStep({
        type: 'change',
        value: 'Song',
        selectors: [
            [
                '#reqName'
            ]
        ],
        target: 'main'
    });
    await runner.runStep({
        type: 'click',
        target: 'main',
        selectors: [
            [
                '#reqArtist'
            ]
        ],
        offsetY: 20.08331298828125,
        offsetX: 214.3333282470703,
    });
    await runner.runStep({
        type: 'change',
        value: 'Artist',
        selectors: [
            [
                '#reqArtist'
            ]
        ],
        target: 'main'
    });
    await runner.runStep({
        type: 'click',
        target: 'main',
        selectors: [
            [
                'button:nth-of-type(2)'
            ],
            [
                'text/Queue'
            ]
        ],
        offsetY: 20.08331298828125,
        offsetX: 27.33331298828125,
    });

    await runner.runAfterAllSteps();
}

if (process && import.meta.url === url.pathToFileURL(process.argv[1]).href) {
    run()
}
