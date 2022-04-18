using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Interfaces.interfaces
{
    internal interface ICheck
    {
        void CheckFrames(int countWheels, int countDoors, IWheel[] wheels, IDoor[] doors,bool doorsAndWheelsUnlimited);
        void CheckWheels(float max_pressure, float min_pressure, float this_pressure);
        void CheckEngine(float max_power, float min_power, float power);
        void CheckBottom(int max_volume, int min_volume, int volume);
        void CheckDictionary(IDictionary<string, object> values, string[] keys);
    }
}
