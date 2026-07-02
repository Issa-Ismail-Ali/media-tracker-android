# Extra Credit Reflection — Design Alignment

**Name:** Issa Ali  
**Date:** July 2, 2026

---

## The Audit

1. The Login screen title was using the primary blue color instead of black like the wireframe, and the spacing between the logo and title didn't match.
2. The Register screen was using a filled SmartDisplay icon instead of the outlined version shown in the wireframe.
3. The Search screen filter chips were using the default Material colors instead of the primary container color when selected.
4. The Bottom Navigation bar wasn't using the light purple indicator pill for the selected item like the wireframe.
5. My Color.kt file had different colors than the design. The primary, secondary, and tertiary colors didn't match the wireframe values.

---

## What You Changed

### Color System

Before this assignment my Color.kt had different colors than the wireframe. I updated the primary, 
secondary, tertiary, and status colors to match the values from the assignment. I also added the 
Want To, In Progress, and Finished colors along with their container colors. Then I updated Theme.kt
so MaterialTheme uses the new color scheme throughout the app.

### Typography

Most of my screens were already using MaterialTheme.typography, but I changed the Login and Register
screen titles to use bold so they matched the wireframe. I also changed some text colors to use 
onSurface instead of the primary color.

### Buttons

The Login and Register buttons were using the default Material button style. I updated both buttons 
to use a 20.dp rounded shape and the primary color from the theme. I also changed the TextButtons at
the bottom to use the primary color like the wireframe.

### Text Fields

I updated every OutlinedTextField on the Login and Register screens to use an 8.dp rounded shape. I 
also changed the focused border color to the primary color and used the outline color when the field
is not focused.

### Other Components

I updated the Search screen colors so the filter chips use the primary container color when 
selected. I also updated the BottomNavBar so the selected item uses the primary container indicator 
with the primary color for the icon and text. I adjusted the Search screen colors to better match 
the wireframe.

---

## What Was Hard

The hardest part was getting the colors to match the wireframe exactly. At first I thought changing 
Color.kt would automatically update every screen, but some components were still using their default
Material colors. I had to go through the Login, Register, Search, and Bottom Navigation screens and 
update the colors, shapes, and button styles individually until they matched the design.

---

## What You Understand Now

I understand that MaterialTheme controls most of the colors and typography for the entire app. 
Color.kt defines the colors, Theme.kt tells MaterialTheme which colors to use, and then each 
Composable can use MaterialTheme.colorScheme and MaterialTheme.typography instead of hardcoding 
values. I also learned that some components like Buttons, and OutlinedTextFields need 
their colors and shapes set manually if you want them to match a specific design.

---

## Self-Assessment

| Section | Possible | My Estimate |
|:---|:---:|:---:|
| Color System | 13 | 13 |
| Typography | 5 | 5 |
| Component Styling | 15 | 14 |
| Navigation & Cards | 5 | 5 |
| Reflection | 12 | 12 |
| **Total** | **50** | **49** |

**One thing I think I did well:**

I think I did a good job updating the colors across the app so they matched the wireframe much 
better than before. The Login and Register screens especially look much closer to the design.

**One thing I know I left incomplete or could have done better:**

I think I could still improve the spacing and some of the Search screen layout to match the 
wireframe even more closely. The colors are much better now, but there are still a few small design 
differences.