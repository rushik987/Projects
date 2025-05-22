using System.Text;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes; 

namespace FinalRushikPatel
{
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {
        Context db;

        public MainWindow()
        {
            InitializeComponent();
            db = new Context();
        }

        private void Window_Loaded(object sender, RoutedEventArgs e)
        {
            GetAllCities();

            cmbCountries.ItemsSource = db.Countries.ToList();
            cmbCountries.DisplayMemberPath = "CountryName";
            cmbCountries.SelectedValuePath = "CountryId";
        }

        public void GetAllCities()
        {
            grdCities.ItemsSource = db.Cities.ToList();
        }

        private void btnSearch_Click(object sender, RoutedEventArgs e)
        {
            var cityName = txtCityName.Text;

            var cityFound = (from c in db.Cities
                             join country in db.Countries on c.CountryId equals country.CountryId
                             where c.CityName.ToLower().Contains(cityName)
                             select new
                             {
                                 c.CityId,
                                 c.CityName,
                                 c.IsCapital,
                                 c.Population,
                                 Country = country.CountryName
                             }).ToList();

            if (cityFound != null)
            {
                grdCities.ItemsSource = cityFound.ToList();
            }
            else
            {
                MessageBox.Show("City Not Found");
            }
        }

        private void cmbCountries_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {
            if (cmbCountries.SelectedValue != null)
            {
                int countryId = (int)cmbCountries.SelectedValue;

                var results = (from city in db.Cities
                               join country in db.Countries on city.CountryId equals country.CountryId
                               where city.CountryId == countryId
                               select new
                               {
                                   city.CityId,
                                   city.CityName,
                                   city.IsCapital,
                                   city.Population,
                                   Country = country.CountryName
                               }).ToList();

                grdCities.ItemsSource = results;
            }
        }
    }
}