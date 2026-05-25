# Diudiu-News

An Android news application developed with Java and Android Studio.

The app supports real-time news browsing, category management, keyword search, reading history, favorites, offline caching, and AI-generated news summaries powered by GLM.

## Features

### News Feed

- Fetch news from online APIs
- Pull-to-refresh support
- Real-time search
- Infinite scrolling
- News category filtering

### News Details

- Detailed news pages
- Display source and publication time
- Video content support
- AI-generated summaries using GLM

### Personal Records

- Reading history
- Favorite news collection
- Read-status tracking
- Offline access to previously viewed content

### Category Management

- Add custom categories
- Remove categories
- Dynamic category switching
- Multi-category filtering

### Local Storage

- SQLite database support
- Offline news cache
- Persistent reading history
- Favorite management

## Tech Stack

- Java
- Android Studio
- RecyclerView
- SQLite
- OkHttp
- SharedPreferences
- GLM API

## Project Structure

```
app/
├── MainActivity.java
├── Button2.java
├── Button3.java
├── NewsDetail.java
├── TitleAdapter.java
├── FavouriteManager.java
├── RecordManager.java
├── NewsDataBase.java
└── NewsContent.java
```

## Highlights

- Efficient RecyclerView implementation using the ViewHolder pattern
- Asynchronous network requests with OkHttp
- SQLite-based offline storage
- AI-powered news summarization
- Dynamic category management
- Read-history and favorite synchronization
