// console.log("Script loaded");

// // change theme work
// let currentTheme = getTheme();
// //initial -->

// document.addEventListener("DOMContentLoaded", () => {
//   changeTheme();
// });

// //TODO:
// function changeTheme() {
//   //set to web page

//   changePageTheme(currentTheme, "");
//   //set the listener to change theme button
//   const changeThemeButton = document.querySelector("#theme_change_button");

//   changeThemeButton.addEventListener("click", (event) => {
//     let oldTheme = currentTheme;
//     console.log("change theme button clicked");
//     if (currentTheme === "dark") {
//       //theme ko light
//       currentTheme = "light";
//     } else {
//       //theme ko dark
//       currentTheme = "dark";
//     }
//     console.log(currentTheme);
//     changePageTheme(currentTheme, oldTheme);
//   });
// }

// //set theme to localstorage
// function setTheme(theme) {
//   localStorage.setItem("theme", theme);
// }

// //get theme from localstorage
// function getTheme() {
//   let theme = localStorage.getItem("theme");
//   return theme ? theme : "light";
// }

// //change current page theme
// function changePageTheme(theme, oldTheme) {
//   //localstorage mein update karenge
//   setTheme(currentTheme);
//   //remove the current theme

//   if (oldTheme) {
//     document.querySelector("html").classList.remove(oldTheme);
//   }
//   //set the current theme
//   document.querySelector("html").classList.add(theme);

//   // change the text of button
//   document
//     .querySelector("#theme_change_button")
//     .querySelector("span").textContent = theme == "light" ? "Dark" : "Light";
// }

// //change page change theme

// Wrap everything in an IIFE to prevent duplicate variable declarations
(function() {
    console.log("Script loaded");

    // change theme work
    let currentTheme = getTheme();
    //initial -->

    document.addEventListener("DOMContentLoaded", () => {
        changeTheme();
    });

    //TODO:
    function changeTheme() {
        //set to web page
        changePageTheme(currentTheme, "");
        //set the listener to change theme button
        const changeThemeButton = document.querySelector("#theme_change_button");

        changeThemeButton.addEventListener("click", (event) => {
            let oldTheme = currentTheme;
            console.log("change theme button clicked");
            if (currentTheme === "dark") {
                //theme ko light
                currentTheme = "light";
            } else {
                //theme ko dark
                currentTheme = "dark";
            }
            console.log(currentTheme);
            changePageTheme(currentTheme, oldTheme);
        });
    }

    //set theme to localstorage
    function setTheme(theme) {
        localStorage.setItem("theme", theme);
    }

    //get theme from localstorage
    function getTheme() {
        let theme = localStorage.getItem("theme");
        return theme ? theme : "light";
    }

    //change current page theme
    function changePageTheme(theme, oldTheme) {
        //localstorage mein update karenge
        setTheme(theme);
        //remove the current theme

        if (oldTheme) {
            document.querySelector("html").classList.remove(oldTheme);
        }
        //set the current theme
        document.querySelector("html").classList.add(theme);

        // change the text of button
        const button = document.querySelector("#theme_change_button");
        if (button) {
            const span = button.querySelector("span");
            if (span) {
                span.textContent = theme == "light" ? "Dark" : "Light";
            }
        }
    }
})(); // End of IIFE