# USD to BRL Currency Widget

A modern, always-on-top Java Swing currency widget that displays real-time USD to Brazilian Real (BRL) exchange rates with a sleek technological interface.

## 🚀 Features

- **Always-on-Top**: Widget stays visible above all other windows
- **Real-time Exchange Rates**: USD to BRL conversion with high precision
- **Auto-refresh**: Updates every 5 minutes automatically
- **Modern UI**: Dark technological theme with cyan accents
- **Draggable**: Click and drag to move anywhere on screen
- **Live Clock**: Real-time clock display in header
- **Connection Status**: Visual indicators for API connectivity
- **Manual Refresh**: Click the "R" button to update immediately
- **Error Handling**: Graceful fallback when API is unavailable

## 🎨 Interface

- **Dark Theme**: Deep space blue background for modern look
- **Color Coded Status**: 
  - 🟢 Green: Connected and updating
  - 🟡 Yellow: Updating/Loading
  - 🔴 Red: Offline/Error
- **Frameless Design**: Clean, borderless window
- **Professional Typography**: Segoe UI and Consolas fonts

## 🛠️ How to Run

1. **Compile the application:**
   ```bash
   javac CurrencyWidget.java
   ```

2. **Run the application:**
   ```bash
   java CurrencyWidget
   ```

## 📋 Requirements

- Java 8 or higher
- Internet connection for exchange rate data
- Windows/Mac/Linux compatible

## 🔧 Technical Details

- **API Source**: ExchangeRate-API for reliable currency data
- **Update Frequency**: Every 5 minutes (300 seconds)
- **Precision**: 2 decimal places for BRL rates
- **Timeout**: 5-second connection timeout for responsiveness
- **Memory Management**: Proper timer cleanup on exit

## 🎯 Usage

1. Launch the widget - it will appear as a small, dark window
2. The widget automatically fetches and displays the current USD to BRL rate
3. Drag the window to position it anywhere on your screen
4. The rate updates automatically every 5 minutes
5. Click "R" to manually refresh the rate
6. Click "X" to close the application

## 📊 Rate Accuracy

The widget provides highly accurate exchange rates that update every 24 hours from the API source. Minor differences from real-time sources (like Google) are normal due to market fluctuations and update frequencies.

## 🔒 Privacy

- No data is stored or transmitted beyond fetching exchange rates
- No personal information is collected
- Completely offline operation except for rate updates