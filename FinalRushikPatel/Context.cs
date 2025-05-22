using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Microsoft.EntityFrameworkCore;

namespace FinalRushikPatel
{
    public class Context : DbContext
    {
        public DbSet<Country> Countries { get; set; }

        public DbSet<City> Cities { get; set; }

        protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
        {
            optionsBuilder.UseSqlServer(@"Server=(LocalDB)\MSSQLLocalDB;Database=FinalRushikPatel;Trusted_Connection=True;MultipleActiveResultSets=True;");
        }

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            modelBuilder.Entity<Country>().HasData(
                new Country { CountryId = 1, CountryName = "Canada" },
                new Country { CountryId = 2, CountryName = "Australia" },
                new Country { CountryId = 3, CountryName = "England" }
            );

            modelBuilder.Entity<City>().HasData(
                new City { CityId = 1, CityName = "Toronto", IsCapital = false, Population = 3000000, CountryId = 1 },
                new City { CityId = 2, CityName = "Ottawa", IsCapital = true, Population = 1000000, CountryId = 1 },
                new City { CityId = 3, CityName = "Vancouver", IsCapital = false, Population = 700000, CountryId = 1 },
                new City { CityId = 4, CityName = "Sydney", IsCapital = false, Population = 5000000, CountryId = 2 },
                new City { CityId = 5, CityName = "Melbourne", IsCapital = false, Population = 5200000, CountryId = 2 },
                new City { CityId = 6, CityName = "London", IsCapital = true, Population = 9600000, CountryId = 3 },
                new City { CityId = 7, CityName = "Manchester", IsCapital = false, Population = 570000, CountryId = 3 }
            );
        }
    }
}
