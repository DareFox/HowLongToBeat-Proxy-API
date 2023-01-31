import express from 'express'

const port = 1337
const app = express()

app.get('/', (req, res) => {
    res.send("Sleepin' on a subway. City up above me. Dreamin' up the words to this song. Bettin' on a someday. Wakin' up at someplace. Believe me, baby, I know")
})


app.listen(port, () => {
    console.log(`[server]: Server is running at http://localhost:${port}`);
});