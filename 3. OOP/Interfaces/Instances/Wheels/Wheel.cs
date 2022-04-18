using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Interfaces.interfaces;

namespace Interfaces.Instances.Wheels
{
    class Wheel : IWheel
    {
        string name;
        protected ICheck check;
        string status;
        float pressure;
        float max_pressure;
        float min_pressure;

        public Wheel(string name,float max_pressure,float min_pressure, ICheck check)
        {
            status = "";
            this.max_pressure = max_pressure;
            this.min_pressure = min_pressure;
            this.name = name;
            this.check = check;
        }
        public string GetName()
        {
            return name;
        }

        public float GetPressure()
        {
            return pressure;
        }

        public string GetStatus()
        {
            return status;
        }

        public virtual void init(IDictionary<string, object> args)
        {
            check.CheckDictionary(args, new string[]
            {
                "pressure"
            });
            this.pressure = (float)args["pressure"];
            try
            {
                check.CheckWheels(max_pressure, min_pressure, pressure);
            }
            catch (Exception ex)
            {
                status = ex.Message;
            }
        }

        public float GetMaxPressure()
        {
            return max_pressure;
        }

        public float GetMinPressure()
        {
            return min_pressure;
        }
    }
}
