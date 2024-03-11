from setuptools import setup, find_packages

with open('README.md', 'r') as fh:
    long_description = fh.read()

setup(
    name='DmxPy',
    version='0.5',
    packages=find_packages(),
    entry_points={
        'console_scripts': [
            'dmxpy = dmxpy.DmxPy:main',
        ],
    },
    install_requires=['pyserial'],

    # metadata to display on PyPI
    author='itsb',
    description='DMX USB Pro compatible controller',
    long_description=long_description,
    long_description_content_type='text/markdown',
    license='MIT License',
    keywords='DMX USB Lighting',
    url='https://github.com/itsb/DmxPy',
    classifiers=[
        'Programming Language :: Python',
        'License :: OSI Approved :: MIT License',
        'Operating System :: OS Independent',
        'Environment :: Console',
    ],
)
