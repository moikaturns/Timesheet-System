package timesheet.DTO;

import java.io.Serializable;

import org.joda.time.DateTime;

public class DTOTime implements Serializable {
	DateTime date;
	double logged;
	int projectTimsheetId;
	
	public DTOTime() {
		// TODO Auto-generated constructor stub
	}

	public DateTime getDate() {
		return date;
	}

	public void setDate(DateTime date) {
		this.date = date;
	}

	public double getLogged() {
		return logged;
	}

	public void setLogged(double logged) {
		this.logged = logged;
	}

	public int getProjectTimsheetId() {
		return projectTimsheetId;
	}

	public void setProjectTimsheetId(int projectTimsheetId) {
		this.projectTimsheetId = projectTimsheetId;
	}

	@Override
	public String toString() {
		return "DTOTime [date=" + date + ", logged=" + logged + ", projectTimsheetId=" + projectTimsheetId + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		long temp;
		temp = Double.doubleToLongBits(logged);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + projectTimsheetId;
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
		DTOTime other = (DTOTime) obj;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (Double.doubleToLongBits(logged) != Double.doubleToLongBits(other.logged))
			return false;
		if (projectTimsheetId != other.projectTimsheetId)
			return false;
		return true;
	}

	public DTOTime(DateTime date, double logged, int projectTimsheetId) {
		super();
		this.date = date;
		this.logged = logged;
		this.projectTimsheetId = projectTimsheetId;
	}

}
