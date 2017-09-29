package spider.bean;
import java.util.List;

public class ImageResult {
	
	public int totalFragment;
	public int totalPage;
	public int page;
	public int pagesize;
	public List<Image> ImageList;
	
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
	
	/**
	 * @param totalFragment
	 * @param totalPage
	 * @param page
	 * @param pagesize
	 * @param imageList
	 */
	public ImageResult(int totalFragment, int totalPage, int page,
			int pagesize, List<Image> imageList) {
		super();
		this.totalFragment = totalFragment;
		this.totalPage = totalPage;
		this.page = page;
		this.pagesize = pagesize;
		ImageList = imageList;
	}
	public List<Image> getImageList() {
		return ImageList;
	}
	public void setImageList(List<Image> imageList) {
		ImageList = imageList;
	}
	/**
	 * 
	 */
	public ImageResult() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	


}
