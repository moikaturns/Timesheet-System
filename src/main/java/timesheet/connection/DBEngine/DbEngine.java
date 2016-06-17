package timesheet.connection.DBEngine;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import timesheet.NYIE;
import timesheet.RDNE;
import timesheet.DTO.DTOProject;
import timesheet.DTO.DTOProjectTimeSheet;
import timesheet.DTO.DTOResource;
import timesheet.DTO.DTOTime;
import timesheet.DTO.ViewProjectTimesheet;
import timesheet.connection.ConnectionManager;

public class DbEngine {
	// Lazy day, todo refactor out

	public ViewProjectTimesheet getLoggedTimeByResourceByPojectTS(DTOResource res, DTOProjectTimeSheet projectTS)
			throws SQLException {
		// use SB
		String sql = "SELECT date, timelogged, t. project_timesheet_id FROM time t " + "join project_timesheet pt "
				+ "where t.project_timesheet_id = pt.project_timesheet_id and pt.resource_id = ? and pt.project_timesheet_id = ?";
		List<DTOTime> time = new ArrayList<>();
		try (PreparedStatement ps = ConnectionManager.getConnection().prepareStatement(sql);) {
			ps.setInt(1, res.getResourceId());
			ps.setInt(2, projectTS.getProject_timesheet_id());
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					int i = 1;
					Date date = rs.getDate(i++);
					double timeLogged = rs.getDouble(i++);
					int ptid = rs.getInt(i++);

					time.add(new DTOTime(new DateTime(date), timeLogged, ptid));
				}
			}
		}
		return new ViewProjectTimesheet(time, projectTS);
	}

	public List<DTOProjectTimeSheet> getAllProjectsTimeSheetForResource(DTOResource res) throws SQLException {
		List<DTOProjectTimeSheet> pts = new ArrayList<>();

		String sql = "SELECT project_timesheet_id, resource_id, project_id FROM project_timesheet WHERE resource_id = ?";

		try (PreparedStatement ps = ConnectionManager.getConnection().prepareStatement(sql);) {
			ps.setInt(1, res.getResourceId());
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					int i = 1;
					int ptid = rs.getInt(i++);
					int rid = rs.getInt(i++);
					int pid = rs.getInt(i++);
					pts.add(new DTOProjectTimeSheet(ptid, pid, rid));
				}
			}
		}
		return pts;
	}

	public List<DTOProjectTimeSheet> getProjectTimeSheetForResource(DTOResource res, int project_id)
			throws SQLException {
		List<DTOProjectTimeSheet> pts = new ArrayList<>();

		String sql = "SELECT project_timesheet, resource_id, project_id_id FROM project_timesheet WHERE resource_id = ? AND project_id = ?";

		try (PreparedStatement ps = ConnectionManager.getConnection().prepareStatement(sql);) {
			ps.setInt(1, res.getResourceId());
			ps.setInt(2, project_id);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					int i = 1;
					int ptid = rs.getInt(i++);
					int rid = rs.getInt(i++);
					int pid = rs.getInt(i++);
					pts.add(new DTOProjectTimeSheet(ptid, pid, rid));
				}
			}
		}
		return pts;
	}

	public List<DTOResource> getAllResources() throws SQLException {
		List<DTOResource> resources = new ArrayList<DTOResource>();

		String sql = "SELECT resource_id, resource_name FROM resource";
		try (PreparedStatement ps = ConnectionManager.getConnection().prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				resources.add(new DTOResource(rs.getInt(1), rs.getString(2)));
			}
		}
		return resources;
	}

	public DTOResource getResource(String name) throws SQLException, RDNE {
		String sql = "SELECT resource_id, resource_name FROM resource where resource_name = ?";
		try (PreparedStatement ps = ConnectionManager.getConnection().prepareStatement(sql);) {
			ps.setString(1, name);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					return new DTOResource(rs.getInt(1), rs.getString(2));
				}
			}
		}
		throw new RDNE(name);
	}

	public DTOProject getProject(int projectId) throws SQLException, RDNE {
		String sql = "SELECT project_id, project_name FROM project where project_id = ?";
		try (PreparedStatement ps = ConnectionManager.getConnection().prepareStatement(sql);) {
			ps.setInt(1, projectId);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					return new DTOProject(rs.getInt(1), rs.getString(2));
				}
			}
	}
		throw new RDNE();
	}

	public List<DTOProject> getAllProject() throws SQLException {
		List<DTOProject> projects = new ArrayList<>();
		String sql = "SELECT project_id, project_name FROM project";
		try (PreparedStatement ps = ConnectionManager.getConnection().prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				projects.add(new DTOProject(rs.getInt(1), rs.getString(2)));
			}
		}
		return projects;
	}

	public boolean addProject(String name) throws SQLException {
		String sql = "INSERT INTO project (project_name) VALUES (?)";

		try (Connection connection = ConnectionManager.getConnection();
				PreparedStatement ps = connection.prepareStatement(sql);) {
			ps.setString(1, name);
			int executeUpdate = ps.executeUpdate();
			if (executeUpdate == 1) {
				connection.commit();
				return true;
			} else {
				return false;
			}
		}
	}

	public boolean addProjectTimeSheet(int resourceId, int projectId) throws SQLException {
		String sql = "INSERT INTO project_timesheet (`resource_id`, `project_id`) VALUES (?, ?)";
		try (Connection connection = ConnectionManager.getConnection();
				PreparedStatement ps = connection.prepareStatement(sql);) {
			ps.setInt(1, resourceId);
			ps.setInt(2, projectId);
			int executeUpdate = ps.executeUpdate();
			if (executeUpdate == 1) {
				connection.commit();
				return true;
			} else {
				return false;
			}
		}
	}

	public void saveTimes(List<DTOTime> times, DTOResource resource, DTOProjectTimeSheet projectTimesheet)
			throws SQLException {
		StringBuilder sb = new StringBuilder();
		sb.append(" INSERT INTO time ");
		sb.append(" (date, timelogged, project_timesheet_id) ");
		sb.append(" VALUES ");
		sb.append("   (?, ?, ?) ");
		sb.append(" ON DUPLICATE KEY UPDATE ");
		sb.append("  timelogged     = VALUES(timelogged) ");

		try (Connection connection = ConnectionManager.getConnection();) {
			for (DTOTime dtoTime : times) {
				try (PreparedStatement ps = connection.prepareStatement(sb.toString());) {
					ps.setDate(1, new Date(dtoTime.getDate().getMillis()));
					ps.setDouble(2, dtoTime.getLogged());
					ps.setInt(3, projectTimesheet.getProject_timesheet_id());
					ps.executeUpdate();
				}
			}
			connection.commit();
		}
	}

}
