# What is siege
Siege is spamming server with your request to stress test your server. Very useful to get rare 500 errors

# How to siege
Siege package should be installed via your package manager (aur, apt and etc.)

Syntax is simple:
```
siege -f *file* --concurrent=*number*
```

For local you can use high concurency amount
```bash
siege -f urls_local --concurrent=25
```

For production / online test recommended to use lower concurrency number
```bash
siege -f urls --concurrent=10
```

