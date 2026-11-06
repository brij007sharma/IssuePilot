
import pandas as pd
import random

random.seed(42)

# Each scenario has a title and description.
scenarios = {
    "CRITICAL": [
        ("Production server outage", "The production server is completely unavailable"),
        ("Database failure", "The production database is down and no users can access the application"),
        ("Payment system outage", "All customer payment transactions are failing"),
        ("Security breach", "Unauthorized users have gained access to sensitive production data"),
        ("Application unavailable", "The application is unavailable for every customer"),
        ("Critical data loss", "Important customer records have disappeared from production"),
        ("Authentication outage", "No users can log into the application"),
        ("API outage", "All production API requests are failing"),
        ("Checkout outage", "Customers cannot complete any purchases"),
        ("Production deployment failure", "A deployment has made the entire service unavailable"),
        ("Complete database outage", "The database is completely unavailable and every user is unable to access the application"),
        ("Payment infrastructure outage", "All payment requests are failing and no customer can complete a transaction"),
        ("Production API outage", "The production API is unavailable and all incoming requests fail")
    ],

    "HIGH": [
        ("Document upload failure", "Many users cannot upload important documents"),
        ("Payment processing error", "Some customers experience occasional payment declines, but retries usually succeed and checkout remains available"),
        ("Authentication errors", "Users are frequently receiving login errors"),
        ("Checkout malfunction", "The checkout feature is broken for many customers"),
        ("API endpoint failure", "An important API endpoint is returning server errors"),
        ("Notification failure", "Important notifications are not reaching users"),
        ("Application crashes", "The application crashes during common operations"),
        ("Report generation failure", "Users cannot generate important business reports"),
        ("Search functionality broken", "Many users cannot search for required records"),
        ("Database connection errors", "The application intermittently loses database connections for a subset of requests, while most users can continue working"),
        ("Intermittent database errors", "Database connections fail for some requests but the application remains available to most users"),
        ("Partial payment failures", "Some payment requests fail repeatedly while other customers can complete transactions"),
        ("API errors affecting users", "An important API endpoint fails for a subset of users while the remaining endpoints work")
    ],

    "MEDIUM": [
        ("Slow dashboard", "The dashboard takes several seconds to load"),
        ("Intermittent timeout", "Some requests occasionally time out but work after retrying"),
        ("Incorrect search results", "Search sometimes returns incomplete results"),
        ("Pagination malfunction", "Pagination does not work correctly with large datasets"),
        ("Delayed notifications", "Email notifications are delivered later than expected"),
        ("Profile update issue", "Profile changes sometimes require a page refresh"),
        ("Slow API response", "One non-critical API endpoint responds slightly slower than usual, but requests complete successfully and users can continue working"),
        ("Filter malfunction", "A dashboard filter occasionally displays incorrect results"),
        ("Session timeout issue", "Some users are logged out earlier than expected"),
        ("Image loading delay", "Images occasionally take too long to load"),
        ("Minor database latency", "Database queries take slightly longer than usual but all requests complete successfully"),
        ("Occasional payment delay", "Some payments take longer to process but eventually complete successfully"),
        ("Slow API response", "A non-critical API endpoint responds slowly but remains functional for all users"),
    ],

    "LOW": [
        ("Improve button styling", "Update the visual styling of the submit button"),
        ("Add dark mode", "Introduce a dark mode option in application settings"),
        ("Update documentation", "Add examples and explanations to the documentation"),
        ("Add profile avatar", "Display a profile picture beside the username"),
        ("Improve page spacing", "Adjust spacing between dashboard sections"),
        ("Add tooltips", "Add helpful tooltips to settings icons"),
        ("Change placeholder text", "Update the placeholder text in the search field"),
        ("Add sorting option", "Allow users to sort records by date and name"),
        ("Update footer text", "Change the copyright information in the footer"),
        ("Improve UI responsiveness", "Make the settings page layout more visually consistent"),
        ("Database documentation update", "Improve the documentation describing database configuration and setup"),
        ("Payment page UI improvement", "Improve the visual appearance of the payment page without changing payment functionality"),
        ("API documentation enhancement", "Add examples and descriptions to the API documentation")
    ]
}

# Different wording patterns to create variations.
title_patterns = [
    "{}",
    "Issue reported: {}",
    "Problem with {}",
    "Reported problem - {}",
    "Bug report: {}"
]

description_patterns = [
    "{}.",
    "Users have reported that {}.",
    "The team has identified that {}.",
    "Currently, {}.",
    "An issue has been reported where {}."
]

records = []

for priority, items in scenarios.items():

    for scenario_index, (title, description) in enumerate(items):

        for title_pattern, description_pattern in zip(
                title_patterns, description_patterns
        ):

            records.append({
                "title": title_pattern.format(title),
                "description": description_pattern.format(description),
                "priority": priority,
                "group_id": f"{priority}_{scenario_index}"
            })

# Shuffle records reproducibly.
random.shuffle(records)

df = pd.DataFrame(records)

df.to_csv("data/priority_issues.csv", index=False)

print("Dataset generated successfully!")
print("\nTotal examples:", len(df))

print("\nPriority distribution:")
print(df["priority"].value_counts())

print("\nSample records:")
print(df.head(10).to_string(index=False))
