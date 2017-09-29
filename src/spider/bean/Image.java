package spider.bean;
/**  
 * 图片类   
 *  
 * @author 郑元浩 
 * @date 2016年11月6日
 */
public class Image {
	
	public int imageID;
	public String imageUrl;
	public int imageWidth;
	public int imageHeight;
	public int termID;
	public String termName;
	public String termUrl;
	public String className;
	public String imageScratchTime;
	public int getImageID() {
		return imageID;
	}
	public void setImageID(int imageID) {
		this.imageID = imageID;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public int getImageWidth() {
		return imageWidth;
	}
	public void setImageWidth(int imageWidth) {
		this.imageWidth = imageWidth;
	}
	public int getImageHeight() {
		return imageHeight;
	}
	public void setImageHeight(int imageHeight) {
		this.imageHeight = imageHeight;
	}
	public int getTermID() {
		return termID;
	}
	public void setTermID(int termID) {
		this.termID = termID;
	}
	public String getTermName() {
		return termName;
	}
	public void setTermName(String termName) {
		this.termName = termName;
	}
	public String getTermUrl() {
		return termUrl;
	}
	public void setTermUrl(String termUrl) {
		this.termUrl = termUrl;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getImageScratchTime() {
		return imageScratchTime;
	}
	public void setImageScratchTime(String imageScratchTime) {
		this.imageScratchTime = imageScratchTime;
	}
	/**
	 * @param imageID
	 * @param imageUrl
	 * @param imageWidth
	 * @param imageHeight
	 * @param termID
	 * @param termName
	 * @param termUrl
	 * @param className
	 * @param imageScratchTime
	 */
	public Image(int imageID, String imageUrl, int imageWidth, int imageHeight,
			int termID, String termName, String termUrl, String className,
			String imageScratchTime) {
		super();
		this.imageID = imageID;
		this.imageUrl = imageUrl;
		this.imageWidth = imageWidth;
		this.imageHeight = imageHeight;
		this.termID = termID;
		this.termName = termName;
		this.termUrl = termUrl;
		this.className = className;
		this.imageScratchTime = imageScratchTime;
	}
	/**
	 * 
	 */
	public Image() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	

}
