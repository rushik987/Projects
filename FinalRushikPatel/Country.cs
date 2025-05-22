using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime;
using System.Text;
using System.Threading.Tasks;

namespace FinalRushikPatel
{
    public class Country
    {
        public int CountryId { get; set; }

        public string CountryName { get; set; }

        public ICollection<City> Citties { get; set; }
    }
}
