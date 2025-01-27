package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class BoardDao {
	private static BoardDao instance;
	private BoardDao() {}
	
	public static BoardDao getInstance() {
		if(instance==null)
			instance=new BoardDao();
		return instance;
	}
	
	private Connection getConnection() {
		Connection conn=null;
		try {
			Context ctx=new InitialContext();
			DataSource ds=(DataSource) ctx.lookup("java:comp/env/jdbc/OracleDB");
			conn=ds.getConnection();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return conn;
	}
	
	public int getTotalCnt() throws SQLException {
		int totCnt=0;
		Connection conn=null;
		Statement stmt=null;
		ResultSet rs=null;
		String sql="SELECT COUNT(*) FROM BOARD";
		try {
			conn=getConnection();
			stmt=conn.createStatement();
			rs=stmt.executeQuery(sql);
			
			if(rs.next())
				totCnt=rs.getInt(1);
			System.out.println("BoardDao getTotalCnt-> "+totCnt);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} finally {
			if(conn!=null)
				conn.close();
			if(stmt!=null)
				stmt.close();
			if(rs!=null)
				rs.close();
		}
		return totCnt;
	}
	
	public List<Board> list(int startRow, int endRow) throws SQLException {
		List<Board> list=new ArrayList<Board>();
		Connection conn=null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		System.out.println(startRow);
		System.out.println(endRow);
		String sql="Select * from (select rownum rn ,a.* from "
				+ "(select * from board order by ref desc,re_step) a) "
				+ "where rn between ? and ?";
		
		try {
			conn=getConnection();
			pstmt=conn.prepareStatement(sql);
			pstmt.setInt(1, startRow);
			pstmt.setInt(2, endRow);
			rs=pstmt.executeQuery();
			while(rs.next()) {
				Board board=new Board();
				board.setNum(rs.getInt("num"));
				board.setWriter(rs.getString("writer"));
				board.setSubject(rs.getString("subject"));
				board.setEmail(rs.getString("email"));
				board.setReadcount(rs.getInt("readcount"));
				board.setIp(rs.getString("ip"));
				board.setRef(rs.getInt("ref"));
				// board.setRe_level(rs.getInt("re_step"));
				board.setRe_level(rs.getInt("re_level"));
				board.setRe_step(rs.getInt("re_step"));
				board.setReg_date(rs.getDate("reg_date"));
				list.add(board);
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} finally {
			if(conn!=null)	conn.close();
			if(pstmt!=null)	pstmt.close();
			if(rs!=null)	rs.close();
		}
		return list;
	}
	
	public void readCount(int num) throws SQLException {
		Connection conn=null;
		PreparedStatement pstmt=null;
		String sql="update board set readcount=readcount+1 where num=?";
	
		try {
			conn=getConnection();
			pstmt=conn.prepareStatement(sql);
			pstmt.setInt(1, num);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} finally {
			if(conn!=null)	conn.close();
			if(pstmt!=null)	pstmt.close();
		}
	}
	
	public Board select(int num) throws SQLException {
		Connection conn=null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		String sql="select * from board where num=?";
		Board board=new Board();
		
		try {
			conn=getConnection();
			pstmt=conn.prepareStatement(sql);
			pstmt.setInt(1, num);
			rs=pstmt.executeQuery();
			
			while(rs.next()) {
				board.setNum(rs.getInt("num"));
				board.setWriter(rs.getString("writer"));
				board.setSubject(rs.getString("subject"));
				board.setContent(rs.getString("content"));
				board.setEmail(rs.getString("email"));
				board.setReadcount(rs.getInt("readcount"));
				board.setIp(rs.getString("ip"));
				board.setRef(rs.getInt("ref"));
				board.setRe_step(rs.getInt("re_step"));
				board.setRe_level(rs.getInt("re_level"));
				board.setReg_date(rs.getDate("reg_date"));
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} finally {
			if(conn!=null)	conn.close();
			if(pstmt!=null)	pstmt.close();
			if(rs!=null)	rs.close();
		}
		return board;
	}
	
	public int update(Board board) throws SQLException {
		Connection conn=null;
		PreparedStatement pstmt=null;
		int result=0;
		String sql="update board set subject=?, writer=?, email=?, passwd=?, content=? where num=?";

		try {
			conn=getConnection();
			pstmt=conn.prepareStatement(sql);
			pstmt.setString(1, board.getSubject());
			pstmt.setString(2, board.getWriter());
			pstmt.setString(3, board.getEmail());
			pstmt.setString(4, board.getPasswd());
			pstmt.setString(5, board.getContent());
			pstmt.setInt(6, board.getNum());
			result=pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} finally {
			if(conn!=null)	conn.close();
			if(pstmt!=null)	pstmt.close();
		}
		return result;
	}
	
	public int delete(int num, String passwd) throws SQLException {
		Connection conn=null;
		Statement stmt=null;
		ResultSet rs=null;
		String dbPasswd=null;
		int result=0;
		
		String sql1="select passwd from board where num='"+num+"'";
		String sql2="delete from board where num='"+num+"'";
		
		try {
			conn=getConnection();
			stmt=conn.createStatement();
			rs=stmt.executeQuery(sql1);
			
			if(rs.next()) {
				dbPasswd=rs.getString(1);
				if(dbPasswd.equals(passwd)) {
					stmt.close();
					stmt=conn.createStatement();
					result=stmt.executeUpdate(sql2);
				}
				else
					result=0;
			} else
				result=-1;
			
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} finally {
			if(conn!=null)	conn.close();
			if(stmt!=null)	stmt.close();
			if(rs!=null)	rs.close();
		}
		return result;
	}
	
	public int insert(Board board) throws SQLException {
		
		Connection conn=null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		String sql1="select nvl(max(num), 0) from board";
		String sql2="insert into board values(?,?,?,?,?,?,?,?,?,?,?,sysdate)";
		String sql3="update board set re_step=re_step+1 where ref=? and re_step>?";
		int result=0;
		int num=board.getNum();
		
		try {
			conn=getConnection();
			// 댓글 처리
			if(num!=0) {
				pstmt=conn.prepareStatement(sql3);
				pstmt.setInt(1, board.getRef());
				pstmt.setInt(2, board.getRe_step());
				pstmt.executeUpdate();
				pstmt.close();
				board.setRe_step(board.getRe_step()+1);
				board.setRe_level(board.getRe_level()+1);
			}
			
			
			pstmt=conn.prepareStatement(sql1);
			rs=pstmt.executeQuery();
			rs.next();
			int number=rs.getInt(1)+1;
			rs.close();
			pstmt.close();
			
			if(num==0) board.setRef(number);	// 댓글 대비용
			pstmt=conn.prepareStatement(sql2);
			pstmt.setInt(1, number);
			pstmt.setString(2, board.getWriter());
			pstmt.setString(3, board.getSubject());
			pstmt.setString(4, board.getContent());
			pstmt.setString(5, board.getEmail());
			pstmt.setInt(6, board.getReadcount());	// integer 값은 입력안하면 0값이 들어가기때문
			pstmt.setString(7, board.getPasswd());
			pstmt.setInt(8, board.getRef());
			pstmt.setInt(9, board.getRe_step());
			pstmt.setInt(10, board.getRe_level());
			pstmt.setString(11, board.getIp());
			result=pstmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if(conn!=null)	conn.close();
			if(pstmt!=null)	pstmt.close();
			if(rs!=null)	rs.close();
		}
		return result;
	}
	
}
