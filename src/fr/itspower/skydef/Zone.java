package fr.itspower.skydef;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

public class Zone {
	
	private final int minX, minY, minZ, maxX, maxY, maxZ;
	
	public Zone(Location c1, Location c2) {
		this(c1.getBlockX(), c1.getBlockY(), c1.getBlockZ(), c2.getBlockX(), c2.getBlockY(), c2.getBlockZ());
	}
	
	public Zone(int x1, int y1, int z1, int x2, int y2, int z2) {
		if (x1 < x2 || x1 == x2) {
			minX = x1;
			maxX = x2;
		} else {
			minX = x2;
			maxX = x1;
		}
		
		if (y1 < y2 || y1 == y2) {
			minY = y1 < 0 ? 0 : y1;
			maxY = y2 > 255 ? 255 : y2;
		} else {
			minY = y2 < 0 ? 0 : y1;
			maxY = y1 > 255 ? 255 : y2;
		}
		
		if (z1 < z2 || z1 == z2) {
			minZ = z1;
			maxZ = z2;
		} else {
			minZ = z2;
			maxZ = z1;
		}
	}
	
	public String toString() {
		return minX+","+minY+","+minZ+"|"+maxX+","+maxY+","+maxZ;
	}
	
	public boolean isInArea(Location l) {
		return isInArea(l.getBlockX(), l.getBlockY(), l.getBlockZ());
	}
	
	public boolean isInArea(int x, int y, int z) {
		return x >= minX && x <= maxX && y >= minY && y <= maxY && z >= minZ && z <= maxZ;
	}
	
	public static Zone fromString(String s) {
		
		String[] ss;
		ss = s.split("|", 2);
		
		String[] i, a;
		i = ss[0].split(",", 3);
		a = ss[1].split(",", 3);
		
		int iX = 0, iY = 0, iZ = 0, aX = 0, aY = 0, aZ = 0;
		
		try {
			iX = Integer.parseInt(i[0]);
			iY = Integer.parseInt(i[1]);
			iZ = Integer.parseInt(i[2]);
			
			aX = Integer.parseInt(a[0]);
			aY = Integer.parseInt(a[1]);
			aZ = Integer.parseInt(a[2]);
			
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		
		return new Zone(iX, iY, iZ, aX, aY, aZ);
	}
	
	public boolean hasWool() {
		World w = Bukkit.getWorld("world");
		for (int x = minX; x <= maxX; x++) {
			for (int y = minY; y <= maxY; y++) {
				for (int z = minZ; z <= maxZ; z++) {
					if(w.getBlockAt(x, y, z).getType().equals(Material.WOOL)) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
