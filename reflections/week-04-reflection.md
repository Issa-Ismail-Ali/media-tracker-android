# Week {{04}} Reflection

**Name:** Issa Ali
**Date:** 6/11/2026

---

## Commits This Week

https://github.com/Issa-Ismail-Ali/media-tracker-android/pull/4


---

## Code Review

<!-- Every week you leave a review on a pod mate's pull request. Fill in both parts below.
     Part 1 is the link — I will verify the review exists on GitHub.
     Part 2 is your written assessment — what you actually looked at and what you found. -->

**Reviewed:** *(pod mate's name)* Kenan Port
**Link to my review:**
https://github.com/Zabzar22/media-tracker-android/pull/4#issuecomment-4686714449

### What I Looked At

I looked at my pod mate's RegisterScreen.kt code. I focused on how the registration interface was 
set up and how the different text fields and buttons worked together to collect information from the user.

### What I Noticed

I noticed that the Register Screen used var to store information entered by the user, such as the 
email and password fields. Since I am still learning Kotlin seeing var used throughout the code 
helped me recognize that it is commonly used when values need to change while the software is running. 
I also noticed that the overall structure of the Register Screen looked very similar to my own 
implementation which made it easier for me to understand how the different parts of the interface 
worked together.

### Comments I Left

I commented that the Register Screen appears to be functional and well organized. Based on my review
the text fields and buttons seemed to be connected appropriately, and the screen appeared capable of
collecting the information needed for account registration. Since the implementation was similar to 
my own it also reinforced my understanding of how a registration interface can be structured.

---

## One Thing I Understood More Deeply

One thing I understood more deeply while working on the Register Screen was how 
Spacer(Modifier.height(8.dp)) works. At first, I thought it was just a random line of code, but I 
learned that it creates empty space between elements on the interface. Without it, the text fields 
and buttons would appear crowded together. The 8.dp determines how much space is added.

---

## One Thing I'm Still Confused About

One thing I am still confused about is Kotlin because I am still new to the language. As I work 
through the Register Screen and other parts of the project, I understand the overall goal of what 
the software is supposed to do, but I sometimes struggle with the syntax and understanding why 
certain pieces of code are written in specific ways. For example, I am still learning when to use 
different keywords, how functions work together, and how information moves through the software. 
Sometimes I can follow examples from class or from my professor, but I need more practice to fully 
understand how everything connects behind the scenes. Even though I am still confused about some 
parts of Kotlin, I have become more comfortable reading the code and identifying what different 
sections are responsible for. I know that learning a new programming language takes time, and I 
expect my understanding to improve as I continue working on projects and writing more code.
---

## Anything Else *(optional)*

I helped one of my pod mates catch up on the work they missed from the previous week. We went over 
the material together, discussed the progress that had been made on the project, and reviewed some 
of the code so they could better understand what had been completed.

---

## Rubric

*You don't need to self-assess — this is here so you know what I'm looking at.*

| Section | Points | Full Credit | Half Credit | No Credit |
|:---|:---:|:---|:---|:---|
| **Reflection** | 10 | Specific, honest responses to "More Deeply" and "Still Confused" sections. Shows genuine thinking — not just "I learned X." | Responses are present but vague or generic ("I got better at Compose"). | Missing or one-word answers. |
| **Code Review** | 10 | Specific observation about the code with explanation of why it matters (or a substantive positive comment). Link to review present and verified. | A question or comment that shows you read the code, but lacks explanation. | "Looks good!" or equivalent. Missing link. Review not found on GitHub. |
| **Total** | **20** | | | |

**A note on the code review score:** I check that the review actually exists on GitHub before grading. The written summary here and the GitHub comment should match. If the review isn't there, the written summary can't earn credit.
