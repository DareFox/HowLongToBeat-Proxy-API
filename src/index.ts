import express from 'express'
import { queryGames } from "./hltb/query";

const port = 1337
const app = express()

app.get('/', async (req, res) => {
    res.send("Sleepin' on a subway. City up above me. Dreamin' up the words to this song. Bettin' on a someday. Wakin' up at someplace. Believe me, baby, I know")
})

app.get('/api/query', async (req, res) => {
    const title = req.query.title
    if (title) {
        res.json(await queryGames(title as string))
    } else {
        res.status(400).send({
            message: "No title argument"
        })
    }
})

app.listen(port, () => {
    console.log(`[server]: Server is running at http://localhost:${port}`);
});