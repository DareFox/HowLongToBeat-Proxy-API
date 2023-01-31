import { WeakLRUCache, LRFUExpirer } from 'weak-lru-cache'

export const cache = new WeakLRUCache({
    expirer: new LRFUExpirer({
        cleanupInterval: 3600000 // 1hr
    })
})