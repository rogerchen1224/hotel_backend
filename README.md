# hotel_backend

## API call examples
- http://localhost:9000/hotel/Bangkok?&apiKey=12345
- http://localhost:9000/hotel/Bangkok?sortBy=price&sortOrder=asc&apiKey=12345
- http://localhost:9000/hotel/Bangkok?sortBy=price&sortOrder=desc&apiKey=12345

## Configurations
### conf/api-rate-limit.conf
apiKey=1 Request per n seconds
```
1234=10
```

### conf/application.conf
```
# Default API Rate Limit in seconds
api.rateLimit.defaultRateLimitInSec=5

# Suspended Time in seconds
api.rateLimit.suspendedTimeInSec=30
```