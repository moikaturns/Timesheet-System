package timesheet.DTO;

import java.util.List;

public class ViewProjectTimesheet {
	List<DTOTime> time;
	DTOProjectTimeSheet pts;
	@Override
	public String toString() {
		return "ViewProjectTimesheet [time=" + time + ", pts=" + pts + "]";
	}
	public ViewProjectTimesheet(List<DTOTime> time, DTOProjectTimeSheet pts) {
		super();
		this.time = time;
		this.pts = pts;
	}
	public List<DTOTime> getTime() {
		return time;
	}
	public void setTime(List<DTOTime> time) {
		this.time = time;
	}
	public DTOProjectTimeSheet getPts() {
		return pts;
	}
	public void setPts(DTOProjectTimeSheet pts) {
		this.pts = pts;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((pts == null) ? 0 : pts.hashCode());
		result = prime * result + ((time == null) ? 0 : time.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ViewProjectTimesheet other = (ViewProjectTimesheet) obj;
		if (pts == null) {
			if (other.pts != null)
				return false;
		} else if (!pts.equals(other.pts))
			return false;
		if (time == null) {
			if (other.time != null)
				return false;
		} else if (!time.equals(other.time))
			return false;
		return true;
	}
}
