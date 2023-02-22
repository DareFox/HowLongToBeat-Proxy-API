#!/bin/fish

set games $(cat allGames.txt)

rm urls
rm urls_local

for game in $games
    echo $game
    set encoded $(echo $game | urlencode)
    echo "https://hltb-proxy.fly/v1/query?title=$encoded" | tee -a urls
    echo "http://localhost:1337/v1/query?title=$encoded" | tee -a urls_local

end

set ids $(seq 120000)
for id in $ids 
    echo "https://hltb-proxy.fly.dev/v1/overview?id=$id" | tee -a urls >/dev/null
    echo "http://localhost:1337/v1/overview?id=$id" | tee -a urls_local >/dev/null
end
