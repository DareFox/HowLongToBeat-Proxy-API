import Fastify from "fastify";
import { queryGames } from "./hltb/query";
import { resolve } from "path";
import * as glob from "glob";

const port = 1337;
export const app = Fastify({
    logger: true,
});

glob.sync("./routes/**/*").forEach((file) => {
    app.register(require(resolve(file)));
});

app.register(require("./routes/query"));

app.get("/", async (req, res) => {
    res.send(
        "Sleepin' on a subway. City up above me. Dreamin' up the words to this song. Bettin' on a someday. Wakin' up at someplace. Believe me, baby, I know"
    );
});

const start = async () => {
    try {
        await app.listen({ port: port });
    } catch (err) {
        app.log.error(err);
        process.exit(1);
    }
};

start();
