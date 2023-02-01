import Fastify from "fastify";
import { queryGames } from "./hltb/query";

const port = 1337;
const app = Fastify({
    logger: true,
});

app.get("/", async (req, res) => {
    res.send(
        "Sleepin' on a subway. City up above me. Dreamin' up the words to this song. Bettin' on a someday. Wakin' up at someplace. Believe me, baby, I know"
    );
});


app.route<{
    Querystring: {
        title: string
    },
}>({
    method: 'GET',
    url: '/api/query',
    handler: async (request, reply) => {
        const title = request.query.title;
        if (title) {
            throw Error('test')
            reply.send(await queryGames(title));
        } else {
            reply.status(400).send({
                message: "No title argument",
            });
        }
    }
})

const start = async () => {
    try {
        await app.listen({ port: port });
    } catch (err) {
        app.log.error(err);
        process.exit(1);
    }
};

start();
