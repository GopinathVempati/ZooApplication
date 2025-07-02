# 🦁 Android Zoo Application

This is a sample Android application that fetches animal data using the [API Ninjas Animals API](https://api-ninjas.com/api/animals). It demonstrates the use of **Clean Architecture**, **Jetpack Compose**, **RoomDB**, **StateFlow**, and **Dagger Hilt** in a modern Android project.

---

## 📱 Features

**Search** animals by name or common name
Automatically fetches animals from three categories: `dog`, `bird`, `bug`
**Caches results** using Room for 10 minutes
Displays:
    - All: Name, Phylum, Scientific Name
    - Dogs: Slogan, Lifespan
    - Birds: Wingspan, Habitat
    - Bugs: Prey, Predators
Automatically fetches new data if cache is expired
- 🔁 **Orientation Support**:
    - Portrait → vertically scrolling list
    - Landscape → horizontally scrolling list (💡 Bonus)
- ✅ Clearable **search bar with 'X' icon**
- 🧪 Unit + UI tests for filtering and UI rendering

---

## 📐 Architecture

Follows a Clean Architecture structure:


---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|------------|
| UI | Jetpack Compose, StateFlow |
| Architecture | MVVM + Clean Architecture |
| Network | Retrofit + Gson |
| Caching | RoomDB |
| DI | Dagger Hilt |
| Async | Kotlin Coroutines |
| Testing | JUnit, Compose UI Test, Coroutines Test |

---

## 🧪 Testing

- ✅ Unit tests for ViewModel filtering logic using `kotlinx-coroutines-test`
- ✅ UI tests for:
    - Search filtering behavior
    - Clear button
    - Landscape/portrait rendering
- 🧪 Test tags (`Modifier.testTag()`) are used to reliably find nodes

---

## 🔑 API Access

The app uses the [API Ninjas Animals API](https://api-ninjas.com/api/animals). Make sure to:

1. Get an API key from [api-ninjas.com](https://api-ninjas.com/)
2. Add the following to your `local.properties` or `BuildConfig`:

```properties
API_KEY=your_api_key
BASE_URL=https://api.api-ninjas.com/v1/
