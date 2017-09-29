package spider.bean;
import java.util.List;

public class TextResult {
	
	public int totalFragment;
	public int totalPage;
	public int page;
	public int pagesize;
	public List<Text> TextList;
	
	public int getTotalFragment() {
		return totalFragment;
	}
	public void setTotalFragment(int totalFragment) {
		this.totalFragment = totalFragment;
	}
	public int getTotalPage() {
		return totalPage;
	}
	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public int getPagesize() {
		return pagesize;
	}
	public void setPagesize(int pagesize) {
		this.pagesize = pagesize;
	}
	public List<Text> getTextList() {
		return TextList;
	}
	public void setTextList(List<Text> textList) {
		TextList = textList;
	}
	/**
	 * @param totalFragment
	 * @param totalPage
	 * @param page
	 * @param pagesize
	 * @param textList
	 */
	public TextResult(int totalFragment, int totalPage, int page, int pagesize,
			List<Text> textList) {
		super();
		this.totalFragment = totalFragment;
		this.totalPage = totalPage;
		this.page = page;
		this.pagesize = pagesize;
		TextList = textList;
	}
	/**
	 * 
	 */
	public TextResult() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	
	


}
