--- 0 没有令牌桶配置
--- -1 表示取令牌失败，也就是桶里没有令牌
--- 1 表示取令牌成功
local function acquire(key, permits, curr_mill_sec)
    local rate_limit_info = redis.pcall("HMGET", key, "last_mill_sec", "curr_permits", "max_permits", "rate")
    local last_mill_sec = rate_limit_info[1]
    local curr_permits = rate_limit_info[2]
    local max_permits = rate_limit_info[3]
    local rate = rate_limit_info[4]

    local local_curr_permits = max_permits

    if (type(last_mill_sec) ~= 'boolean' and last_mill_sec ~= nil) then
        local reverse_permits = math.floor((curr_mill_sec - last_mill_sec) / 1000 * rate)
        local expect_curr_permits = reverse_permits + curr_permits
        local_curr_permits = math.min(expect_curr_permits, max_permits)

        if (reverse_permits > 0) then
            redis.pcall("HSET", key, "last_mill_sec", curr_mill_sec)
        end
    else
        redis.pcall("HSET", key, "last_mill_sec", curr_mill_sec)
    end

    local result = -1
    if (local_curr_permits - permits > 0) then
        result = 1
        redis.pcall("HSET", key, "curr_permits", local_curr_permits - permits)
    else
        redis.pcall("HSET", key, "curr_permits", local_curr_permits)
    end
    return result
end


local function acquireIncr(key, limit)
    local current = tonumber(redis.call("GET", key) or "0")
    if current + 1 > limit then
        return -1
    else
        redis.call("INCRBY", key, 1)
        redis.call("EXPIRE", key, 2)
        return 1
    end
end

local function unlock(key, value)
    if redis.call("get",key) == value then
        return redis.call("del",key)
    else
        return 0
    end
end

local key = KEYS[1]
local method = ARGV[1]
local permits = tonumber(ARGV[2])
local curr_mill_sec = tonumber(ARGV[3])
local limit = tonumber(ARGV[4])

--local key = 'acquireIncr'
--local method = 'acquireIncr'
--local permits = 0
--local curr_mill_sec = 0
--local limit = 3

if method == 'acquire' then
    return acquire(key, permits, curr_mill_sec)
elseif method == 'acquireIncr' then
    return acquireIncr(key, limit)
elseif method == 'unlock' then
    return unlock(key, ARGV[2])
else
    --ignore
end
