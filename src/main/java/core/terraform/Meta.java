package core.terraform;

public class Meta {
  public Integer limit;
  public Integer current_offset;
  public Integer next_offset;
  public String next_url;

  public Integer getLimit() {
    return limit;
  }

  public void setLimit(Integer limit) {
    this.limit = limit;
  }

  public Integer getCurrent_offset() {
    return current_offset;
  }

  public void setCurrent_offset(Integer current_offset) {
    this.current_offset = current_offset;
  }

  public Integer getNext_offset() {
    return next_offset;
  }

  public void setNext_offset(Integer next_offset) {
    this.next_offset = next_offset;
  }

  public String getNext_url() {
    return next_url;
  }

  public void setNext_url(String next_url) {
    this.next_url = next_url;
  }
}
