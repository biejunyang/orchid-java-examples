package com.orchid.redis.example.service;

import com.orchid.redis.example.model.Article;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Random;

public class ArticleServiceTest {

    private static ArticleService articleService;

    @BeforeClass
    public static void init(){
        articleService=new ArticleService();
    }


    @Test
    public void testPostArticle(){
        Random random=new Random();
        for(int i=1; i<=100; i++){
            Article article=new Article();
            article.setTitle("文章"+i);
            article.setLink("http://wxdcec.ocm+"+i+"sc");
            article.setPosterId((long)random.nextInt(100));
            articleService.postArticle(article);
        }
    }


    @Test
    public void testGetArticle(){
        Random random=new Random();
        long articleId=(long)random.nextInt(100);
        System.out.println(articleService.getArticle(articleId));
        articleId=(long)random.nextInt(100);
        System.out.println(articleService.getArticle(articleId));
    }


    @Test
    public void testRemoveArticle(){
        for(int i=1; i<=1000; i++){
            articleService.deleteArticle(i);
        }
    }

    @Test
    public void testVoteArticle(){
        Random random=new Random();
        long articleId=random.nextInt(100);
        articleService.voteArticle(articleId, 12);
        System.out.println(articleId);
    }

}
