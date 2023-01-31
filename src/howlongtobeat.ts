export async function queryGames(title: string): Promise<HLTB_SearchResponse[]> {
    const queryTerms = title.trim().split(' ') 

    const queryJson = {
        searchType: "games",
        searchTerms: queryTerms,
        searchPage: 1,
        size: 20,
        searchOptions: {
            games: {
                userId: 0,
                platform: "",
                sortCategory: "popular",
                rangeCategory: "main",
                rangeTime: { min: null, max: null },
                gameplay: { perspective: "", flow: "", genre: "" },
                rangeYear: { min: "", max: "" },
                modifier: "",
            },
            users: { sortCategory: "postcount" },
            filter: "",
            sort: 0,
            randomizer: 0,
        },
    };

    const response = await fetch("https://howlongtobeat.com/api/search", {
        method: 'POST',
        headers: {
            'Authority': 'howlongtobeat.com',
            'Content-Type': 'application/json',
            'Origin': 'https://howlongtobeat.com',
            'Referer': `https://howlongtobeat.com/?q=${encodeURIComponent(title.trim())}`,
            'User-Agent': 'Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.0.0 Safari/537.36'
        },
        body: JSON.stringify(queryJson)
    })

    const json = await response.json()
    const convertedResults: HLTB_SearchResponse[] = (json.data as any[]).map((gameJson) => {
        const steamID: number | null = (gameJson["profile_steam"] != 0) ? gameJson["profile_steam"] : null
        const obj: HLTB_SearchResponse = {
            gameName: gameJson["game_name"],
            type: gameJson["game_type"],
            gameId: gameJson["game_id"],
            gameImage: gameJson["game_image"],
            dev: gameJson["profile_dev"],
            platforms: gameJson["profile_platform"].split(", "),
            releaseYear: gameJson["release_world"],
            beatTime: {
                main: {
                    avgSeconds: gameJson["comp_main"],
                    polledCount: gameJson["comp_main_count"]
                },
                extra: {
                    avgSeconds: gameJson["comp_plus"],
                    polledCount: gameJson["comp_plus_count"]
                },
                completionist: {
                    avgSeconds: gameJson["comp_100"],
                    polledCount: gameJson["comp_100_count"]
                },
                all: {
                    avgSeconds: gameJson["comp_all"],
                    polledCount: gameJson["comp_all_count"]
                }
            },
            counters: {
                beated: gameJson["count_comp"],
                speedruns: gameJson["count_speedrun"],
                backlogs: gameJson["count_backlog"],
                reviews: gameJson["count_review"],
                retired: gameJson["count_retired"]
            },
            steamId: steamID
        }
        return obj
    })

    return convertedResults
}

export interface HLTB_SearchResponse {
    gameName: string;
    type: string;
    gameId: number;
    gameImage: string;
    dev: string;
    platforms: string[];
    releaseYear: number;
    beatTime: {
        main: {
            avgSeconds: number;
            polledCount: number;
        };
        extra: {
            avgSeconds: number;
            polledCount: number;
        };
        completionist: {
            avgSeconds: number;
            polledCount: number;
        };
        all: {
            avgSeconds: number;
            polledCount: number;
        };
    };
    counters: {
        beated: number;
        speedruns: number;
        backlogs: number;
        reviews: number;
        retired: number;
    };
    steamId: number | null;
}
