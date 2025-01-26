# Fake Store App

This is a sample e-commerce application that allows users to browse products, view details, and add them to the cart. It uses Firebase for authentication and Firestore for storing data. The app fetches products from a remote API and displays them in a user-friendly interface built using Jetpack Compose.

## Features

- **User Authentication**: Firebase Authentication for user login and registration.
- **Product Browsing**: Users can browse a list of products with their details like title, price, and rating.
- **Product Details**: On clicking a product, users can view detailed information such as description, price, and an option to add the product to the cart.
- **Search Functionality**: Users can search for products based on their query.
- **Add to Cart**: Users can add items to the cart for later purchase.
- **Buy Now**: Users can make purchases directly from the product detail screen.

## Technologies Used

- **Kotlin**: Primary programming language.
- **Jetpack Compose**: UI framework used for building native Android UIs.
- **Firebase**: 
    - **Firebase Authentication**: Used for user authentication (login/sign-up).
    - **Firebase Firestore**: Used for storing product data.
- **Hilt**: Dependency injection library for clean architecture.
- **Navigation Component**: Used to navigate between different screens in the app.
- **Coroutines**: Used for handling asynchronous tasks.

## Setup

### Prerequisites

Before you can run the app, ensure you have the following:

- **Android Studio**: Version 4.0 or higher.
- **Android SDK**: Make sure the latest SDK is installed.
- **Firebase Project**: Set up a Firebase project for authentication and Firestore services.

### Steps to Run the App

1. **Clone the Repository**

   ```bash
   git clone [https://github.com/your-username/fake-store-app.git](https://github.com/rezaul-web/Fake-Store.git)
