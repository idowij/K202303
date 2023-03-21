package com.learn.study.presentation;

import com.learn.study.domain.Pop;
import com.learn.study.apllication.BlogService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.util.HtmlUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.learn.study.KakaoRestApiHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 화면 연결 Controller
 */
@RequiredArgsConstructor
@Controller
public class BlogController {

    private final BlogService blogService;
    private final KakaoRestApiHelper kakaoApiHelper;

    @GetMapping("/")
    public String index() {

        return "index";
    }

    //블로그 검색
    @GetMapping("/posts/search")
    public String search(String keyword, String page, String searchKey, String dataSort, Model model)  {
        try {
        	kakaoApiHelper.setAccessToken("4fdcc63a3c3dd525665f5bbd427044fb");
        	page = kakaoApiHelper.isInteger(page) ? page : "1";
        	dataSort = "recency".equals(dataSort) ? "recency" : "accuracy";
        	
        	Map<String, String> paramMap;
        	paramMap = new HashMap<String, String>();
        	paramMap.put("query", keyword);
        	paramMap.put("sort", dataSort);
        	paramMap.put("page", page);
        	paramMap.put("size", "10");
        	
        	String jsonString = kakaoApiHelper.searchBlog(paramMap);
        	
        	Gson gson = new Gson();
        	Map<String, Object> jsonObject = gson.fromJson(jsonString, new TypeToken<Map<String, Object>>(){}.getType());
        	List<Map<String, Object>> searchList = (List) jsonObject.get("documents");
        	
        	//검색결과가 없거나 오류발생시 첫 페이지로 이동(네이버 API 추가필요)
        	if(searchList == null) {
        		return "/";
        	}
        
        	for(int i = 0; i < searchList.size(); i++) {
        		//title html 제거
        		String text = (String) searchList.get(i).get("title"); 
        		text = text.replaceAll("<[^>]*>", " ");
        		text = HtmlUtils.htmlUnescape(text);
        		searchList.get(i).put("title", text);
        		
        		//contents html 제거
        		text = (String) searchList.get(i).get("contents"); 
        		text = text.replaceAll("<[^>]*>", " ");
        		text = HtmlUtils.htmlUnescape(text);
        		searchList.get(i).put("contents", text);
        		
        		//datetime 변환 2023-01-12T14:35:00.000+09:00
        		text = (String) searchList.get(i).get("datetime");
        		text = text.length() >= 10 ? text.substring(0, 10) : text;
        		searchList.get(i).put("datetime", text);
        	}
        	
        	//검색버튼으로 검색할 때 검색횟수 증가 
        	if("Y".equals(searchKey)){
        		Pop pop = Pop.builder().keyword(keyword).view(1).build();
        		blogService.incView(pop);
        	}
        	
        	Map<String, Object> meta = (Map<String, Object>)jsonObject.get("meta");
        	String is_end = meta.get("is_end").toString();
        	double pageable_count = (double)meta.get("pageable_count");
        	double total_count = (double) meta.get("total_count");
        	
        	model.addAttribute("searchList", searchList);
        	model.addAttribute("keyword", keyword);
        	model.addAttribute("previous", Integer.parseInt(page) - 1);
        	model.addAttribute("next", Integer.parseInt(page) + 1);
        	model.addAttribute("hasPrev", (Integer.parseInt(page) <= 1) ? false :true);
        	model.addAttribute("hasNext", !Boolean.parseBoolean(is_end));
        	model.addAttribute("dataSort", dataSort);
        	model.addAttribute("recency", "recency".equals(dataSort) ? "recency" : null);
        	model.addAttribute("accuracy", "accuracy".equals(dataSort) ? "accuracy" : null);
        	model.addAttribute("curPage", page);
        	
        	return "posts/posts-search";
        } catch (Exception e) {
        	//e.printStackTrace();
        	return "/";
        }
    }
    
    /* 인기검색어 조회 */
    @GetMapping("/posts/rank")
    public ResponseEntity read() {
    	List<Pop> list = blogService.rank();
        return ResponseEntity.ok(list);
    }
}
