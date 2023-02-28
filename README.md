# Current endpoints:

- #### GET /v1/query
##### Search game by title
Example: **https://hltb-proxy.fly.dev/v1/query?title=Edna**

URL Params:
- title: string, **required**
- page: int

Codes: 
- 200 -	Sucesss
- 500 -	Server error

----
- #### GET /v1/overview
##### Get info about game
Example: **https://hltb-proxy.fly.dev/v1/overview?id=3059**

URL Params:
- id: long, **required**

Codes: 
- 200 -	Sucesss
- 500 -	Server error
- 404 - Game with specified not found

----
- #### GET /v1/cache
##### Get info about current cache state
Example: **https://hltb-proxy.fly.dev/v1/cache**

Codes: 
- 200 -	Sucesss
- 500 -	Server error
