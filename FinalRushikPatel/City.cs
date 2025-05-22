using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace FinalRushikPatel
{
    public class City
    {
        public int CityId { get; set; }
        public string CityName { get; set; }

        public bool IsCapital { get; set; }

        public long Population {  get; set; }

        public int CountryId { get; set; }

        public Country Country { get; set; }
    }
}
