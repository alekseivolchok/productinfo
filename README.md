# Starting the application

Launching the application through console (Windows):

```
.\mvnw spring-boot:run
```

Health check
```
curl http://localhost:8080/api/v1/health
```

Simple test request (provided you have trade.csv file prepared)
```
curl --request POST -d '@trade.csv' --header 'Content-Type: text/csv' --header 'Accept: text/csv' http://localhost:8080/api/v1/enrich
```
In Windows PowerShell you might want to do it this way:
```
curl.exe '--request' 'POST' '-d' '@trade.csv' '--header' 'Content-Type: text/csv' '--header' 'Accept: text/csv' 'http://localhost:8080/api/v1/enrich'
```

Due to possible issues with OS/Server encoding and understanding of how line breaks work it's recommended to use a more robust tool than curl.
Note that "Accept" header was in the requirements but it's not a great idea to use it as it hides all the error messages from you unless we form error messages as CSV file which is not very convenient.

# Environments

Set environmental variable ENV to "dev" or "prod" to switch between dev and prod configuration.

# Potential additions

- In general a lot can be made more generic in anticipation of more types of user input. Make DateValidator a little more complex to support datetime instead of just date in case of schema changes or future new data.
- Storing CSV data in map (AbstractCsvSearcher) might not be the best idea in the long run. It can be replaced by a more specialized tool, perhaps supporting advanced search techniques like fuzzy search in case we're dealing with something beyond numbers.
- User request processing could be nicer with more detailed responses on errors. I'd like to make a clear response telling user if something is wrong with the request or on our side.
- CSV processing looks inelegant to me. There must be a complete working solution that at least hides all the ugliness, but I suppose this exercise wouldn't be much of an exercise if I used something like that.
- Currently any malformed CSV results in no response at all. This might be fine depending on the situation, but it might not be.
- Add localization for messages like "Missing product name".
- Move more magic numbers into configuration files.
- Can never have enough tests!
