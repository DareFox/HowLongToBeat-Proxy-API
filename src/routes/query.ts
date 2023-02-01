import { queryGames } from "../hltb/query";
import { FastifyInstance } from "fastify";


async function query(app: FastifyInstance) {
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
                reply.send(await queryGames(title));
            } else {
                reply.status(400).send({
                    message: "No title argument",
                });
            }
        }
    })
}

module.exports = query