package com.orchid.redis.example.service;

import com.orchid.redis.example.model.Article;
import redis.clients.jedis.Jedis;

public class ArticleService {
    //存储文章id的键名，值的存储类型为列表
    private final static String ARTICLE_ID_KEY_NAME="article:";
    private final static Long VOTE_SCORE=432L;
    private final static String SCORE_ZSET_KEY="score:";//根据文章评分进行排序，有序集合：成员是文章id、排序分值则是评分
    private final static String VOTES_ZSET_KEY="votes:";//根据文章票数进行排序，有序集合：成员是文章id、排序分值则是评分，
    private final static String CREATE_TIME_ZSET_KEY="time:";//根据文章创建时间进行排序，有序集合：成员是文章id、排序分值则是评分，

    private final static long ONW_WEEK_Millis=7*24*60*60*1000;
    private final static String VOTED_USER_KEY="voted:";//某篇文章的投票人数集合



    /**
     * 发布文章
     * @return
     */
    public long postArticle(Article article){

        Jedis jedis=new Jedis("localhost");
        //创建文章
        long articleId=jedis.incr(ARTICLE_ID_KEY_NAME);
        String articleKey=ARTICLE_ID_KEY_NAME+articleId;
        int votes=1;//初始票数，1票，自己投票
        long time=System.currentTimeMillis();
        double score=votes*VOTE_SCORE+time;
        jedis.hset(articleKey, "id", articleId+"");
        jedis.hset(articleKey, "title", article.getTitle());
        jedis.hset(articleKey, "link", article.getLink());
        jedis.hset(articleKey, "posterId", article.getPosterId().toString());
        jedis.hset(articleKey, "time", time+"");
        jedis.hset(articleKey, "votes", votes+"");
        jedis.hset(articleKey, "score", score+"");

        //根据评分排序
        jedis.zadd(SCORE_ZSET_KEY, score, articleId+"");

        //根据票数排序
        jedis.zadd(VOTES_ZSET_KEY, votes, articleId+"");

        //根据创建时间排序
        jedis.zadd(CREATE_TIME_ZSET_KEY, time, articleId+"");

        //记录文章的投票人集合
        jedis.sadd(VOTED_USER_KEY+articleId, article.getPosterId().toString());
        jedis.expireAt(VOTED_USER_KEY+articleId, time+ONW_WEEK_Millis);
        jedis.close();
        return articleId;
    }


    /**
     * 文章投票
     * @param articleId
     * @param votedId
     */
    public void  voteArticle(long articleId, long votedId){
        Jedis jedis=new Jedis("localhost");
        Article article=getArticle(articleId);
        long timeOff=System.currentTimeMillis()-article.getTime();
        if(timeOff > ONW_WEEK_Millis){
            return;//超过一周不能投票
        }

        if(jedis.sadd(VOTED_USER_KEY+articleId, String.valueOf(votedId))>0){
            jedis.hincrBy(ARTICLE_ID_KEY_NAME+articleId, "votes", 1);
            jedis.hset(ARTICLE_ID_KEY_NAME+articleId, "score", String.valueOf(article.getScore()+VOTE_SCORE));

            jedis.zincrby(VOTES_ZSET_KEY, 1, articleId+"");//
            jedis.zincrby(SCORE_ZSET_KEY, VOTE_SCORE, articleId+"");
        }
    }



    /**
     * 删除文章
     * @param articleId
     */
    public void deleteArticle(long articleId){
        Jedis jedis=new Jedis();
        //删除文章
        jedis.del(ARTICLE_ID_KEY_NAME+articleId);

        //删除评分排序中的文章id
        jedis.zrem(SCORE_ZSET_KEY, articleId+"");

        //删除票数培训中的文章id
        jedis.zrem(VOTES_ZSET_KEY, articleId+"");

        //删除创建时间排序中的文章id
        jedis.zrem(CREATE_TIME_ZSET_KEY, articleId+"");

        jedis.close();
    }

    /**
     * 根据id获取文章
     * @param articleId
     * @return
     */
    public Article getArticle(long articleId){
        Jedis jedis=new Jedis("localhost");
        Article article=new Article();
        article.setId(articleId);
        article.setTitle(jedis.hget(ARTICLE_ID_KEY_NAME+articleId, "title"));
        article.setLink(jedis.hget(ARTICLE_ID_KEY_NAME+articleId, "link"));
        article.setPosterId(Long.valueOf(jedis.hget(ARTICLE_ID_KEY_NAME+articleId, "posterId")));
        article.setTime(Long.valueOf(jedis.hget(ARTICLE_ID_KEY_NAME+articleId, "time")));
        article.setVotes(Integer.valueOf(jedis.hget(ARTICLE_ID_KEY_NAME+articleId, "votes")));
        article.setScore(Double.valueOf(jedis.hget(ARTICLE_ID_KEY_NAME+articleId, "score")));

        return article;
    }



}
